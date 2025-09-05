import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static class PaginationResult {
        private final Map<Integer, String> items;

        public PaginationResult(Map<Integer, String> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, String> entry : items.entrySet()) {
                sb.append("key=")
                    .append(entry.getKey())
                    .append(", value=")
                    .append(entry.getValue())
                    .append("\n");
            }
            return sb.toString();
        }
    }

    Map<Integer, String> database = new HashMap<>();
    Map<String, List<Integer>> cache = new HashMap<>();

    public App() {
        initDb();
    }

    private void initDb() {
        List<Integer> indexesAsc = new ArrayList<>();
        List<Integer> indexesDesc = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            database.put(i, "item-" + i);
            indexesAsc.add(i);
            indexesDesc.addFirst(i);
        }

        cache.put("sort=asc", indexesAsc);
        cache.put("sort=desc", indexesDesc);
    }

    // query: sort=asc or sort=desc
    public PaginationResult getNextPage(String query, int cursor, int pageSize) {
        if (!cache.containsKey(query)) {
            throw new IllegalArgumentException("query = " + query + " is not currently supported");
        }

        List<Integer> list = cache.get(query);
        // int cursorIdx = list.indexOf(cursor); // O(n)
        int cursorIdx = Collections.binarySearch(list, cursor); // O(log n)
        if (cursorIdx == -1) {
            throw new IllegalArgumentException("invalid cursor: " + cursor);
        }

        int startIndex = cursorIdx + 1;
        int endIndex = Math.min(startIndex + pageSize, list.size()); // end is exclusive
        Map<Integer, String> items = new HashMap<>();
        for (int i = startIndex; i < endIndex; i++) {
            int idx = list.get(i);
            items.put(idx, database.get(idx));
        }

        return new PaginationResult(items);
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        App app = new App();
        PaginationResult result = app.getNextPage("sort=asc", 2, 10);
        System.out.println(result);
    }
}
