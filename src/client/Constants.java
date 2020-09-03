package client;

public class Constants {
    private Constants(){}
    
    public static final String LOGIN = "login";
    public static final String CREATE_ACCOUNT = "create_account";
    public static final String CHAT_CLIENT = "chat_client";

    public static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/chat_application";
    public static final String POSTGRES_USER = "postgres";
    public static final String POSTGRES_PASSWORD = "postgres";

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 28000;

    public static final int WIDTH = 600;
    public static final int HEIGHT = 450;

	public static final char CONTROL_CHAR = (char) 05;
    public static final char SEPARATE_CHAR = (char) 06;
}