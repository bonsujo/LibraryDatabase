package ca.sheridancollege.database;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;

@Repository
public class DatabaseAccess {

	@Autowired
	private NamedParameterJdbcTemplate jdbc;

	public DatabaseAccess(DataSource dataSource) {
		this.jdbc = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<String> getAuthorities() {

		// create a new instance of MapSqlParameterSource for our use
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "SELECT DISTINCT authority FROM authorities";

		// query the database
		List<String> authorities = jdbc.queryForList(query, namedParameters, String.class);

		return authorities;
	}

	public List<Book> getBooks() {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "SELECT * FROM books";

		// Will map a row coming in to an instance of Student
		BeanPropertyRowMapper<Book> bookMapper = new BeanPropertyRowMapper<>(Book.class);

		List<Book> books = jdbc.query(query, namedParameters, bookMapper);

		return books;

	}

	public List<Review> getReviews(Long bookId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "SELECT * FROM reviews WHERE bookId = :bookId ";

		namedParameters.addValue("bookId", bookId);

		BeanPropertyRowMapper<Review> reviewMapper = new BeanPropertyRowMapper<>(Review.class);

		List<Review> reviews = jdbc.query(query, namedParameters, reviewMapper);

		return reviews;

	}

	public Book getBook(Long id) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "SELECT * FROM books WHERE id = :id";

		namedParameters.addValue("id", id);

		BeanPropertyRowMapper<Book> bookMapper = new BeanPropertyRowMapper<Book>(Book.class);

		List<Book> books = jdbc.query(query, namedParameters, bookMapper);

		if (books.isEmpty()) {
			return null; // error condition
		} else {
			return books.get(0); // will only have one
		}
	}

	public int addBook(Book book) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "INSERT INTO books (title,author) VALUES(:title, :author)";

		namedParameters.addValue("title", book.getTitle()).addValue("author", book.getAuthor());

		int returnValue = jdbc.update(query, namedParameters);
		return returnValue;

	}

	public Long addReview(Review review) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		String query = "INSERT INTO reviews (text, bookId) VALUES (:text, :bookId)";

		namedParameters.addValue("text", review.getText()).addValue("bookId", review.getBookId());

		KeyHolder generatedKey = new GeneratedKeyHolder();

		// using the 3-arg version of update
		int returnValue = jdbc.update(query, namedParameters, generatedKey);

		// get the key and send it back if successful
		Long bookId = (Long) generatedKey.getKey();

		return (returnValue > 0) ? bookId : 0;

	}
}
