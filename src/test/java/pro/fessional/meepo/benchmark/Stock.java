package pro.fessional.meepo.benchmark;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class Stock {

    public final String name;
    public final String name2;
    public final String url;
    public final String symbol;
    public final double price;
    public final double change;
    public final double ratio;

    public Stock(String name, String name2, String url, String symbol, double price, double change, double ratio) {
        this.name = name;
        this.name2 = name2;
        this.url = url;
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.ratio = ratio;
    }

    public String getName() {
        return this.name;
    }

    public String getName2() {
        return this.name2;
    }

    public String getUrl() {
        return this.url;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public double getPrice() {
        return this.price;
    }

    public double getChange() {
        return this.change;
    }

    public double getRatio() {
        return this.ratio;
    }

}
