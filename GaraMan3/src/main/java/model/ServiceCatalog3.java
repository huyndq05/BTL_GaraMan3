package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public final class ServiceCatalog3 {
    private static final Map<Integer, Category> CATALOG = load();

    private ServiceCatalog3() {}

    private static Map<Integer, Category> load() {
        InputStream resource = ServiceCatalog3.class.getResourceAsStream("/service-catalog.tsv");
        if (resource == null) throw new IllegalStateException("Missing service-catalog.tsv");
        Map<Integer, Category> categories = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
            Category current = null;
            List<Option> items = null;
            for (String line; (line = reader.readLine()) != null;) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] row = line.split("\t", -1);
                switch (row[0]) {
                    case "service":
                        current = new Category(row[2]);
                        if (categories.put(Integer.parseInt(row[1]), current) != null) {
                            throw new IllegalArgumentException("Duplicate service ID");
                        }
                        items = current.items;
                        break;
                    case "group":
                        Group group = new Group(row[1], row[2], Integer.parseInt(row[3]));
                        current.groups.add(group);
                        items = group.items;
                        break;
                    case "item":
                        items.add(new Option(row[1], Integer.parseInt(row[2])));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown service catalog row: " + row[0]);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read service catalog", e);
        }
        return categories;
    }

    public static boolean contains(int serviceId) {
        return CATALOG.containsKey(serviceId);
    }

    public static String forScreen(boolean showPrices) {
        StringJoiner result = new StringJoiner(",", "{", "}");
        for (Map.Entry<Integer, Category> entry : CATALOG.entrySet()) {
            Category category = entry.getValue();
            String values;
            if (category.groups.isEmpty()) {
                values = "\"type\":\"flat\"," + itemFields(category.items, showPrices);
            } else {
                StringJoiner groups = new StringJoiner(",", "[", "]");
                for (Group sub : category.groups) {
                    String price = showPrices ? ",\"price\":" + sub.price : "";
                    groups.add("{\"key\":" + quote(sub.key) + ",\"name\":" + quote(sub.name)
                            + price + "," + itemFields(sub.items, showPrices) + "}");
                }
                values = "\"type\":\"nested\",\"groups\":" + groups;
            }
            result.add(quote(entry.getKey().toString()) + ":{\"name\":" + quote(category.name) + "," + values + "}");
        }
        return result.toString();
    }

    private static String itemFields(List<Option> options, boolean showPrices) {
        StringJoiner names = new StringJoiner(",", "[", "]");
        StringJoiner prices = new StringJoiner(",", "{", "}");
        for (Option option : options) {
            names.add(quote(option.name));
            if (showPrices) prices.add(quote(option.name) + ":" + option.price);
        }
        return "\"items\":" + names + (showPrices ? ",\"prices\":" + prices : "");
    }

    private static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("<", "\\u003c").replace("\u2028", "\\u2028").replace("\u2029", "\\u2029") + "\"";
    }

    public static float price(Service3 service, String selection) {
        if (selection == null || selection.isEmpty()) return 0;
        Category category = CATALOG.get(service.getId());
        if (category == null) throw new IllegalArgumentException("Unknown service");
        if (category.groups.isEmpty()) return priceOfItems(category.items, selection);

        int total = 0;
        Set<String> seen = new HashSet<>();
        for (String section : selection.split("; ", -1)) {
            Group match = null;
            for (Group group : category.groups) {
                if (section.equals(group.name) || section.startsWith(group.name + ": ")) {
                    match = group;
                    break;
                }
            }
            if (match == null || !seen.add(match.key)) throw new IllegalArgumentException("Invalid service group");
            total += section.equals(match.name)
                    ? match.price
                    : priceOfItems(match.items, section.substring(match.name.length() + 2));
        }
        return total;
    }

    private static int priceOfItems(List<Option> options, String selection) {
        int total = 0;
        Set<String> seen = new HashSet<>();
        while (!selection.isEmpty()) {
            Option match = null;
            for (Option option : options) {
                if ((selection.equals(option.name) || selection.startsWith(option.name + ", "))
                        && (match == null || option.name.length() > match.name.length())) {
                    match = option;
                }
            }
            if (match == null || !seen.add(match.name)) throw new IllegalArgumentException("Invalid service item");
            total += match.price;
            if (selection.equals(match.name)) return total;
            selection = selection.substring(match.name.length() + 2);
        }
        throw new IllegalArgumentException("Missing service item");
    }

    private static class Option {
        final String name;
        final int price;

        Option(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }

    private static class Group extends Option {
        final String key;
        final List<Option> items = new ArrayList<>();

        Group(String key, String name, int price) {
            super(name, price);
            this.key = key;
        }
    }

    private static class Category {
        final String name;
        final List<Option> items = new ArrayList<>();
        final List<Group> groups = new ArrayList<>();

        Category(String name) {
            this.name = name;
        }
    }
}
