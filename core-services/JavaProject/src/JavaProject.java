import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JavaProject {

	public static void main(String[] args) {
		List<String> lst=new ArrayList<>();
		lst.add("sdf");
		lst.add("adf");
		lst.add("cdf");
		lst.add("bdf");
		lst.add("hdf");
		lst.add("tdf");
		lst.stream().parallel().forEachOrdered(action->{
			System.out.println(action);
		});
		Stream.of("AAA","DDD","BBB","CCC").parallel().forEach(s->System.out.println("Output:"+s));
		Stream.of("AAA","DDD","BBB","CCC").parallel().forEachOrdered(s->System.out.println("Output:"+s));
	}
}
