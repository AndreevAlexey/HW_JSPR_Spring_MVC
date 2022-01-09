package REST.repository;

import REST.exception.NotFoundException;
import REST.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Post> removedPosts = new ConcurrentHashMap<>();

    private volatile long postsCnt = 0;

    private synchronized void increaseCnt() {
        postsCnt++;
    }

    private Post addPost(Post post) {
        // новый id
        increaseCnt();
        post.setId(postsCnt);
        // добавляем в мар
        posts.put(postsCnt, post);
        return post;
    }

    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    public Post getById(long id) {
        // пост отсутствует в мар
        if(!posts.containsKey(id)) throw new NotFoundException();
        return posts.get(id);
    }

    public Post save(Post post) {
        long id = post.getId();
        // пост удален
        if (removedPosts.containsKey(id)) throw new NotFoundException();
        // пост присутствует в мар
        if(posts.containsKey(id)) {
            // заменяем
            posts.replace(id, post);
            // иначе просто добавляем с новым id
        } else {
            return addPost(post);
        }
        return post;
    }

    public void removeById(long id) {
        // пост с таким id есть
        if(posts.containsKey(id)) {
            // добавляем в удаленные
            removedPosts.put(id, posts.get(id));
            // удаляем
            posts.remove(id);
        } else throw new NotFoundException();
    }
}
