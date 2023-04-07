package ca.sheridancollege;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.database.DatabaseAccess;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestController {
	
	@Autowired 
	private DatabaseAccess da;
	
	@Autowired
	MockMvc mockMvc;
	
	
	@Test
	public void testRoot() throws Exception{
		
		mockMvc.perform(get("/"))
		.andExpect(status().isOk())
		.andExpect(view().name("index"));
		
	}
	
	@Test 
	public void testViewBook () throws Exception {
		
		List<Book> books = da.getBooks();
		
		Book book = books.get(0);
		Long id = book.getId();
		
		mockMvc.perform(get("/viewBook/{id}", id))
			.andExpect(status().isOk())
			.andExpect(view().name("view-book"));

		
	}

}
