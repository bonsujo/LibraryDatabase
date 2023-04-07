package ca.sheridancollege.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;
import ca.sheridancollege.database.DatabaseAccess;

@Controller
public class HomeController {

	// Instance DatabaseAccess
	@Autowired
	private DatabaseAccess database;

	public HomeController(DatabaseAccess database) {
		this.database = database;
	}

	@GetMapping("/login")
	public String login() {
		return "/login";
	}
	@GetMapping("/registerPage")
	public String goToRegister(Model model) {
		return "/register";
	}
	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}

	@GetMapping("/")
	public String goHome(Model model, @ModelAttribute Book book) {
		model.addAttribute("bookList", database.getBooks());
		return "index";
	}

	@PostMapping("/addBook")
	public String addBook(Model model, @ModelAttribute Book book) throws Exception {
		
		//adds book to table
		database.addBook(book);
		
		// returns index
		return "redirect:/";
	}
	@GetMapping("/admin/addBook")
	public String goToAddBook(Model model, @ModelAttribute Book book) throws Exception {

		//will add new book to model
		model.addAttribute("book", new Book());

		//returns add-book.html
		return "/admin/add-book";
	}

	@GetMapping("/user/addReview/{id}")
	public String goToAddReview(Model model, @PathVariable Long id) {
		
		//New instance of review
		Review review = new Review();
		
		//Sets book id 
		review.setBookId(id);
		
		//adds new review to model
		model.addAttribute("review", review);

		//returns add-review.html
		return "/user/add-review";
		
	}
	
	@PostMapping("/submitReview")
	public String submitReview(@ModelAttribute Review review, Model model, @RequestParam("bookId") Long id) throws Exception {
		//grabs id of book
		Book book = database.getBook(id);
		
		//adds book to model with specific id
		model.addAttribute("book", book);
		
		//adds review inserted from html text area to the model
		database.addReview(review);	
		model.addAttribute("reviewList", database.getReviews(id));

		return "view-book";

	}
	
	@GetMapping("/viewBook/{id}")
	public String viewBooks(@PathVariable Long id, Model model) {
		
		Book book = database.getBook(id);
	
		model.addAttribute("book", book);
		
		//updates the reviewList of book and adds it to the model
		model.addAttribute("reviewList", database.getReviews(id));


		return "view-book";
	}
	
	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public JdbcUserDetailsManager jdbcUserDetailsManager;
	
	
	

	@PostMapping("/register")
	public String addUser(@RequestParam String userName, @RequestParam String password, Model model) {

		//calls get getAuthorities class from database, the authority role (user) will then be added to authorities
		List<String> authorities = database.getAuthorities();
		model.addAttribute("authorities", authorities);
		
		//when user registers they will be given user role
		List<GrantedAuthority> aList = new ArrayList<>();
		aList.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		//converts from literal text format to encoded one to SALT password
		String ePassword = passwordEncoder.encode(password);
		User user = new User(userName, ePassword, aList);
		
		//allows for creation of user
		jdbcUserDetailsManager.createUser(user);
		
		
		//once user registers message will display in model
		model.addAttribute("message", "Thank you for registering!");
		
		model.addAttribute("bookList", database.getBooks());
		
		return "index";
		
	 }
   }

