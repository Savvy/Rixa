import java.util.*;

public class Main {

    private static List<String> list = new ArrayList<>();
    public static void main(String[] args) {
        if (list.isEmpty()) {
            for (int i = 0; i < 25; i++) {
                list.add("Item No. " + (i + 1));
            }
        }
        List<String> page = getPage(list, 3, 5);
        System.out.println("Page: " + (3));
        page.forEach(System.out::println);
        List<List<String>> pages = getPages(list, 5);

        page = pages.get(2);
        System.out.println("Page: " + (3));
        page.forEach(System.out::println);
        /*for(int i = 0; i < pages.size(); i++) {
            List<String> page = pages.get(i);
            System.out.println("Page: " + (i + 1));
            page.forEach(System.out::println);
        }*/
    }

    public static List<String> getPage(List<String> sourceList, int page, int pageSize) {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public static List<List<String>> getPages(Collection<String> c, Integer pageSize) {
        if (c == null)
            return Collections.emptyList();
        List<String> list = new ArrayList<>(c);
        if (pageSize == null || pageSize <= 0 || pageSize > list.size())
            pageSize = list.size();
        int numPages = (int) Math.ceil((double)list.size() / (double)pageSize);
        List<List<String>> pages = new ArrayList<>(numPages);
        for (int pageNum = 0; pageNum < numPages;)
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        return pages;
    }
}
