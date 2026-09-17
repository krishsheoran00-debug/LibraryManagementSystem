package service;

import java.util.List;
import model.Book;

/** Interface demonstrating abstraction — any catalog-like component can implement search. */
public interface Searchable {
    List<Book> search(String keyword);
}
