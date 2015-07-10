package library.storage.eaa;

import library.core.eaa.Activity;
import library.domain.Author;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Kuleshov
 */
public class AuthorActivities {
    public static Activity createGetAuthorsActivity(List<Author> authors) {
        return new GetAuthorsActivity(authors);
    }

    public static Activity createGetAuthorActivity(Author author) {
        return new GetAuthorActivity(author);
    }

    public static Activity createSaveAuthorActivity(Author author) {
        return new SaveAuthorActivity(author);
    }

    public static class GetAuthorsActivity implements Activity {
        private final List<Author> authors;

        public GetAuthorsActivity(List<Author> authors) {
            this.authors = new ArrayList<>(authors);
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.AUTHORS_RETURNED;
        }

        @Override
        public Object getPayload() {
            return authors;
        }
    }

    public static class GetAuthorActivity implements Activity {
        private final Author author;

        public GetAuthorActivity(Author author) {
            this.author = author;
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.AUTHOR_RETURNED;
        }

        @Override
        public Object getPayload() {
            return author;
        }
    }

    public static class SaveAuthorActivity implements Activity {
        private final Author author;

        public SaveAuthorActivity(Author author) {
            this.author = author;
        }

        @Override
        public StorageActivityType getActivityType() {
            return StorageActivityType.AUTHOR_SAVED;
        }

        @Override
        public Object getPayload() {
            return author;
        }
    }
}
