import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Entities.*;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import com.db4o.query.Predicate;

public class TShirtsDB4O {
	public static ArrayList<Order> orders;
	static ObjectContainer db;

	public static void main(String[] args) throws IOException, ParseException {
		TShirtsDB4O TSM = new TShirtsDB4O();
		FileAccessor fa = new FileAccessor();
		fa.readArticlesFile("articles.csv");
		fa.readCreditCardsFile("creditCards.csv");
		fa.readCustomersFile("customers.csv");
		fa.readOrdersFile("orders.csv");
		fa.readOrderDetailsFile("orderDetails.csv");
		orders = fa.orders;
		try {

			File file = new File("orders.db");
			String fullPath = file.getAbsolutePath();
			db = Db4o.openFile(fullPath);

			TSM.addOrders();
			TSM.listOrders();
			TSM.listArticles();
			TSM.addArticle(7, "CALCETINES EJECUTIVOS 'JACKSON 3PK'", "gris", "45", 18.00F);
			TSM.updatePriceArticle(7, 12.00);
			TSM.llistaArticlesByName("CALCETINES EJECUTIVOS 'JACKSON 3PK'");
			TSM.deletingArticlesByName("POLO BÁSICO 'MANIA'");
			TSM.deleteArticleById(7);
			TSM.listArticles();
			TSM.listCustomers();
			TSM.changeCreditCardToCustomer(1);
			TSM.listCustomers();
			TSM.llistaCustomerByName("Laura");
			TSM.showOrdersByCustomerName("Laura");
			TSM.showCreditCardByCustomerName("Laura");
			TSM.deleteCustomerbyId(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.deleteOrderContentById_Order(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.listCustomers();
			TSM.clearDatabase();
			TSM.listOrders();
		} finally {
			db.close();
		}
	}

	/**
	 * Select Customer using customer id and next generate a new credit card and
	 * update customer using the new credit card
	 * 
	 * @param i
	 *            idCustomer
	 */
	public void changeCreditCardToCustomer(int i) {
		System.out.println("\n\nCambiando targeta de credito...");
		String ran1 = "3141241423141892";
		String ran2 = "597";
		int ran3 = 5;
		int ran4 = 17;
		ObjectSet<Customer> result = db.queryByExample(new Customer(i, null, null, null, null, null));
		CreditCard creditCard = new CreditCard(ran1,ran2,ran3,ran4);
		while (result.hasNext()){
			result.next().setCreditCard(creditCard);
		}
	}

	/**
	 * Select Article using id and next update price
	 * 
	 * @param id
	 *            article
	 * @param newPrice
	 */
	public void updatePriceArticle(int id, double newPrice) {
		System.out.println("\n\nActualizando articulos...");
		ObjectSet<Article> result = db.queryByExample(new Article(id, null, null, null, 0.0F));
		while (result.hasNext()){
			result.next().setRecommendedPrice((float) newPrice);
		}
	}

	/** Method to add Articles to the database */
	public void addArticle(int i, String string, String string2, String string3, float d) {
		System.out.println("\n\nAgregando un articulo...");
		Article article = new Article(i, string, string2, string3, d);
		System.out.println(article);
		db.store(article);
	}

	/**
	 * Delete an article using idArticle
	 * 
	 * @param i
	 *            idArticle
	 */
	public void deleteArticleById(int i) {
		ObjectSet<Article> result = db.queryByExample(new Article(i, null, null, null, 0.0F));
		while(result.hasNext()) {
			db.delete(result.next());
		}
	}

	/**
	 * Delete Order and its orderdetails using idOrder
	 * 
	 * @param i
	 *            idOrder
	 */
	public void deleteOrderContentById_Order(int i) {
		ObjectSet<Order> result = db.queryByExample(new Order(i, null,null,null,null));
		while (result.hasNext()){
			db.delete(result.next());
		}
	}

	/**
	 * Select Order using his id and order details
	 * 
	 * @param i
	 *            idOrder
	 */
	public void retrieveOrderContentById_Order(int i) {
		System.out.println("\n\nOrders con la id indicada");
		ObjectSet<Order> result = db.queryByExample(new Order(i, null,null,null,null));
		while (result.hasNext()){
			System.out.println(result.next());
		}

		ObjectSet<Order> result2 = db.queryByExample(new Order(i, null,null,null,null));
		while (result2.hasNext()){
			System.out.println(result2.next().getDetails());
		}
	}

	/**
	 * Delete Customer using idCustomer
	 * 
	 * @param i
	 *            idCustomer
	 */
	public void deleteCustomerbyId(int i) {
		ObjectSet<Customer> result = db.queryByExample(new Customer(i, null, null, null, null, null));
		while(result.hasNext()) {
			db.delete(result.next());
		}
	}

	/**
	 * Select Customer using customer name and next select the credit card
	 * values
	 * 
	 * @param string
	 *            customer name
	 */
	public void showCreditCardByCustomerName(String string) {
		System.out.println("\n\nMuestra informacion de la targeta de credito del cliente indicado");
		ObjectSet<Customer> result = db.queryByExample(new Customer(0, string, null, null, null, null));
		System.out.println(result.next().getCreditCard());

	}

	/**
	 * Method to list Orders and Orderdetails from the database using the
	 * customer name
	 */
	public void showOrdersByCustomerName(String string) {
		System.out.println("\n\nMuestra orders del cliente indicado");
		Customer customer = new Customer(0, string, null, null, null, null);
		ObjectSet<Order> result = db.queryByExample(new Order(0, null, null, customer, null));
		while (result.hasNext()){
			System.out.println(result.next());
		}

		ObjectSet<Order> result2 = db.queryByExample(new Order(0, null, null, customer, null));
		while (result2.hasNext()){
			System.out.println(result2.next().getDetails());
		}
	}

	/** Method to clear the database */
	public void clearDatabase() {
		System.out.println("\n\nLimpiando base de datos...");
		ObjectSet<Article> result = db.queryByExample(Article.class);
		while(result.hasNext()) {
			db.delete(result.next());
		}

		ObjectSet<Order> result2 = db.queryByExample(Order.class);
		while(result2.hasNext()) {
			db.delete(result2.next());
		}

		ObjectSet<Customer> result3 = db.queryByExample(Customer.class);
		while(result3.hasNext()) {
			db.delete(result3.next());
		}

		ObjectSet<CreditCard> result4 = db.queryByExample(CreditCard.class);
		while(result4.hasNext()) {
			db.delete(result4.next());
		}

		ObjectSet<OrderDetail> result5 = db.queryByExample(OrderDetail.class);
		while(result5.hasNext()) {
			db.delete(result5.next());
		}
	}

	/**
	 * Delete Article using article name
	 *
	 * @param string
	 *            Article name
	 */
	public void deletingArticlesByName(String string) {
		System.out.println("\n\nBorrando articulos con el nombre indicado...");
		ObjectSet<Article> result = db.queryByExample(new Article(0, string, null, null, 0.0F));
		while(result.hasNext()) {
			db.delete(result.next());
		}
	}

	/** Method to list Articles from the database using their name */
	public void llistaArticlesByName(String string) {
		System.out.println("\n\nArticulos con el nombre indicado");
		ObjectSet<Article> result = db.queryByExample(new Article(0, string, null, null, 0.0F));
		while(result.hasNext()) {
			System.out.println(result.next());
		}
	}

	/** Method to list Customers from the database using their name */
	public void llistaCustomerByName(String string) {
		System.out.println("\n\nCustomers con el nombre indicado");
		ObjectSet<Customer> result = db.queryByExample(new Customer(0, string, null, null, null, null));
		while(result.hasNext()) {
			System.out.println(result.next());
		}
	}

	/** Method to list Customers from the database */
	public void listCustomers() {
		System.out.println("\n\nCustomer llegits des de la base de datos");
		ObjectSet<Customer> result = db.queryByExample(Customer.class);
		System.out.println(result.size());
		while (result.hasNext()){
			System.out.println(result.next());
		}
	}

	/** Method to list Articles from the database */
	public void listArticles() {
		System.out.println("\n\nArticles llegits des de la base de datos");
		ObjectSet<Article> result = db.queryByExample(Article.class);
		System.out.println(result.size());
		while (result.hasNext()){
			System.out.println(result.next());
		}
	}

	/** Method to add Orders to the database */
	public void addOrders() {
		System.out.println("\n\nAñadiendo orders a la base de datos...");
		for (int i = 0; i < orders.size(); i++) {
			db.store(orders);
		}
	}

	/** Method to list Orders from the database */
	public void listOrders() {
		System.out.println("\n\nOrders llegits des de la base de datos");
		ObjectSet<Order> result = db.queryByExample(Order.class);
		System.out.println(result.size());
		while (result.hasNext()){
			System.out.println(result.next());
		}
	}
}
