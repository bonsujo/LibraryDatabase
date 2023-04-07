package ca.sheridancollege;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;
import ca.sheridancollege.database.DatabaseAccess;

@RunWith(SpringRunner.class)

@SpringBootTest
@AutoConfigureMockMvc
public class TestDatabase {

	@Autowired
	private DatabaseAccess da;
	
	
	@Test
	public void testDatabaseAddBook() {
		
		Book book = new Book();
		book.setTitle("Great Gatsby");
		book.setAuthor("Scott Fitzgerald");
		
		int origSize = da.getBooks().size();
		
		da.addBook(book);
		
		int newSize = da.getBooks().size();
		
		assertThat(newSize).isEqualTo(origSize +1);
		
		
	}
	
	@Test
	public void testDatabaseAddReview() {
		
		Review review = new Review();
		
		Long id = (long) 1;
		
		review.setText("Very good book");
		review.setBookId(id);

		int origSize = da.getReviews(id).size();
		
		da.addReview(review);
		
		int newSize = da.getReviews(id).size();
	
		assertThat(newSize).isEqualTo(origSize + 1);
		
		
	}
	
	
}
