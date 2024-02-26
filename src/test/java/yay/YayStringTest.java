package yay;

import org.junit.jupiter.api.Test;

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
		YayString elem;
		do{
			elem = array.nextArrayValue();
			if(elem != null)
				System.out.println(elem.asFixedIndentString());
		}while(elem != null);
		
		Map.Entry<CharSequence, YayString> extra = ys.nextObjectValue();
		assertEquals(extra.getKey().toString(), "and so on");
		assertEquals(extra.getValue().asSequence().toString(), "right");
	}
}