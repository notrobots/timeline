## Adding a new platform

+ Create a subclass of `Post` that holds the post's data.

```kotlin
@Entity //Required by Room to serialize this entity
class MyPost(
    var title: String,
    var content: String,
    var username: String,
    timestamp: Long,
    profileId: Long
) : Post(timestamp, profileId, "mysocial")
```

+ Create a subclass of `PostWithMedia` that holds the post and its media (images and videos).

// TODO

+ (Room only) Create a DAO for the entity. You can extend the `BaseDao` interface that already defines most of the DAO methods but you need to define the `get` methods youself.

```kotlin
@Dao
interface MyPostDao : BaseDao<MyPost> {
    @Query("SELECT * FROM MyPost")
    suspend fun getPosts(): List<MyPost>
}
```

+ (Room and Hilt/Dagger only) Register the new entity and DAO in the database

```
@Database(
    entities = [
        ...,

        MyPost::class
    ],
    version = 1
)
abstract class TimelineDatabase : RoomDatabase() {
    ...

    abstract fun myPostDao(): MyPostDao
}
```

+ (Room and Hilt/Dagger only) Define the DAO in the database module

```
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun myPostDao(database: Database): MyPostDao {
        return database.myPostDao()
    }

    ...
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