## Adding a new platform

+ Create a subclass of `Post` that holds the post's data.

```kotlin
class MyPost(
    var title: String,
    var content: String,
    var username: String,
    timestamp: Long
) : Post(timestamp, "mysocial")
```

+ (If using Room) Create a DAO for the entity above. You can extend the `BaseDao` interface.

```kotlin
interface MyPostDao : BaseDao<MyPost> {
    @Query("SELECT * FROM MyPost")
    suspend fun getPosts(): List<MyPost>
}
```

+ Create a subclass of `SocialRepository` and specify how the posts for that specific platform are fetched.

```kotlin
class MySocialRepository : SocialRepository<MyPost>() {
    override val posts = myPostDao.getPosts()
    override val profiles = myProfileDao.getProfiles("mysocial")

    override suspend fun refreshPosts(profile: Profile) {
        // Fetch new posts from that profile's feed
    }
}
```