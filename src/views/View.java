package views;

import presenter.BookPresenter;

import java.util.Scanner;

public class View {
    private Scanner in;

    public View() {
        in = new Scanner(System.in);
    }

    public int readInt(String message) {
        System.out.println(message);
        int number = Integer.parseInt(in.nextLine());
        return number;
    }

    public String readString(String message) {
        System.out.println(message);
        String text = in.nextLine();
        return text;
    }

    public boolean readBoolean(String message) {
        System.out.println(message);
        String input = in.nextLine();
        return input.equalsIgnoreCase("true");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showMessageWithBookInfo(String bookInfo) {
        System.out.println(bookInfo);
    }

    public static void main(String[] args) {
        new BookPresenter().run();
    }
}