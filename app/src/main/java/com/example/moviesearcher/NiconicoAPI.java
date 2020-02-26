package com.example.moviesearcher;


public class NiconicoAPI {
    public static final String BASE_URL = "https://api.search.nicovideo.jp/api/v2/snapshot/video/contents/search?q=";
    public final String TARGETS = "&targets=title,description,tags";
    public final String FIELDS = "&fields=contentId,title,description,thumbnailUrl,startTime";
    public final String SORT = "&_sort=-startTime";
    public final String CONTEXT = "&_context=apiguide";

    public final String OFFSET = "&_offset=0";
    public final String LIMIT = "&_limit=10";
}
