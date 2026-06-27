package com.example.stabilityloadingplanner.data.models

// Modelo para pesquisa de artigos no Wikipedia (pageimages)
data class WikiSearchImageResponse(
    val query: WikiSearchImageQuery?
)

data class WikiSearchImageQuery(
    val pages: Map<String, WikiSearchPage>?
)

data class WikiSearchPage(
    val pageid: Int?,
    val title: String?,
    val thumbnail: WikiThumbnail?
)

data class WikiThumbnail(
    val source: String,
    val width: Int,
    val height: Int
)

// Modelo para pesquisa de ficheiros na Wikimedia Commons (imageinfo)
data class WikiCommonsResponse(
    val query: WikiCommonsQuery?
)

data class WikiCommonsQuery(
    val pages: Map<String, WikiCommonsPage>?
)

data class WikiCommonsPage(
    val pageid: Int?,
    val title: String?,
    val imageinfo: List<WikiFileInfo>?
)

data class WikiFileInfo(
    val url: String?,
    val thumburl: String?
)