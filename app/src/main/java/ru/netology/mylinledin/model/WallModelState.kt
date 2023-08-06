package ru.netology.mylinledin.model

import ru.netology.mylinledin.dto.posts.Post

class WallModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)

data class WallPosts(
    var posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)