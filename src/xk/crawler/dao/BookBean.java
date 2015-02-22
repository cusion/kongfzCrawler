package xk.crawler.dao;

public class BookBean {
	private String bookID;
	private String bookName;
	private String author;
	private String publisher;
	private String date;
	private String bookShopID;
	
	public BookBean(){};
	
	public BookBean(String bookID, String bookName, String author,
					String publisher, String date, String bookShopID) {
		this.bookID = bookID;
		this.bookName = bookName;
		this.bookShopID = bookShopID;
		this.publisher = publisher;
		this.author = author;
		this.date = date;
	}
	
	public String getBookID() {
		return bookID;
	}
	public void setBookID(String bookID) {
		this.bookID = bookID;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookShopID() {
		return bookShopID;
	}
	public void setBookShopID(String bookShopID) {
		this.bookShopID = bookShopID;
	}
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
