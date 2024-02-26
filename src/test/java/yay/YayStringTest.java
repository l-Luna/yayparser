package yay;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class YayStringTest{
	
	@Test
	void testBasic(){
		String basic = """
				Here's an example:
				 - At the top level, this example is an object.  An object associates names with
				   values.  Each name ends in the : symbol and is followed by its associated
				   value.  In this example, there's just one name in the object: "Here's an
				   example".
				 - Associated with the "Here's an example" name is this array.  Each entry
				   starts with a - symbol.  Note that the entries in the array can continue onto
				   the next line as long as the indentation matches.
				 - Objects can appear in arrays as well.  Here are some fruits and their colors:
				 - Carrot: Orange
				   Banana: Yellow
				   Eggplant: Purple
				and so on: right
				""";
		YayString ys = new YayString(basic);
		Map.Entry<CharSequence, YayString> arrayObj = ys.nextObjectValue();
		assertEquals(arrayObj.getKey().toString(), "Here's an example");
		
		YayString array = arrayObj.getValue();
		YayString elem = array.nextArrayValue();
		while(elem != null){
			System.out.println(elem.asFixedIndentString());
			elem = array.nextArrayValue();
		}
		
		Map.Entry<CharSequence, YayString> extra = ys.nextObjectValue();
		assertEquals(extra.getKey().toString(), "and so on");
		assertEquals(extra.getValue().asSequence().toString(), "right");
	}
	
	@Test
	void testCollections(){
		String arrayLike = """
				- one!
				- two!
				- three!
				""";
		
		assertEquals(
				new YayString(arrayLike).allArrayValues(x -> x.asSequence().toString()),
				List.of("one!", "two!", "three!")
		);
		
		String objectLike = """
				one: 1
				two: 2
				three:
				 so the story begins with an adventurous keystroke
				 and the creation of a small letter "e"
				""";
		
		assertEquals(
				new YayString(objectLike).allObjectValues(CharSequence::toString, x -> x.asSequence().toString()),
				Map.of(
						"one", "1",
						"two", "2",
						"three", """
								so the story begins with an adventurous keystroke
								 and the creation of a small letter "e\""""
				)
		);
	}
	
	@Test
	void testNestedCollections(){
		String text = """
				-
				 - a
				 - b
				 - c
				-
				 - 1
				 - 2
				 - 3
				   &4
				""";
		List<List<String>> innerLists = new YayString(text).allArrayValues(x -> x.allArrayValues(y -> y.asSequence().toString()));
		assertEquals(innerLists, List.of(List.of("a", "b", "c"), List.of("1", "2", "3\n   &4")));
	}
}