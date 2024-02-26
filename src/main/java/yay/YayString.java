package yay;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class YayString{
	
	@NotNull
	private CharSequence inner;
	private int indent, lineLength;
	
	public YayString(@NotNull CharSequence seq){
		inner = seq;
	}
	
	public YayString(@NotNull CharSequence seq, int indent, int lineLength){
		inner = seq;
		this.indent = indent;
		this.lineLength = lineLength;
	}
	
	@NotNull
	public static YayString empty(){
		return new YayString("");
	}
	
	@NotNull
	public CharSequence asSequence(){
		return inner;
	}
	
	@NotNull
	public String asFixedIndentString(){
		return inner.toString().indent(-indent);
	}
	
	@Nullable("no more characters in the string")
	public Character nextChar(){
		// if the sequence is empty, return EOF
		if(inner.isEmpty())
			return null;
		// get the first byte
		char first = inner.charAt(0);
		// adjust line length
		if(first == '\n')
			lineLength = 0;
		else
			lineLength++;
		// consume the byte
		inner = inner.subSequence(1, inner.length());
		return first;
	}
	
	@NotNull
	public YayString nextValue(){
		// if the sequence is empty, return an empty value
		if(inner.isEmpty())
			return empty();
		// consume empty space between the ':' or '-' and the value
		Character gap = nextChar();
		if(gap == null || !(gap == '\n' || gap == ' '))
			throw new YayParsingException("the '-' or ':' must be separated from the following value by a space or a newline");
		// newline first
		if(gap == '\n'){
			// consume all spaces until we hit a different character
			while(!inner.isEmpty() && inner.charAt(0) == ' ')
				nextChar();
			// if we've reached a different character while still looking for indentation, return empty
			if(lineLength <= indent)
				return empty();
		}
		// value to return
		YayString value = new YayString(inner, lineLength, lineLength);
		while(true){
			if(inner.isEmpty())
				break;
			Character newline = nextChar();
			// keep consuming characters until we hit a newline
			if(newline == null || newline != '\n')
				continue;
			// on a newline: keep consuming spaces
			while(!inner.isEmpty() && inner.charAt(0) == ' ')
				nextChar();
			// if we hit another newline - it's just a blank line - go back to start
			if(!inner.isEmpty() && inner.charAt(0) == '\n'){
				nextChar();
				continue;
			}
			// if we hit a character
			if(lineLength <= indent || inner.isEmpty()){
				int remaining = inner.length() + lineLength + 1;
				value.inner = value.inner.subSequence(0, value.inner.length() - remaining);
				break;
			}
		}
		return value;
	}
	
	@Nullable("no more values in the array")
	public YayString nextArrayValue(){
		if(inner.isEmpty())
			return null;
		if(lineLength != indent)
			throw new YayParsingException("incorrect indentation for array value (off by " + Math.abs(lineLength - indent) + ")");
		Character dash = nextChar();
		if(dash == null || dash != '-')
			throw new YayParsingException("missing '-' to start array element");
		return nextValue();
	}
	
	@Nullable("no more values in the object")
	public Map.Entry<CharSequence, YayString> nextObjectValue(){
		if(inner.isEmpty())
			return null;
		if(lineLength != indent)
			throw new YayParsingException("incorrect indentation for object value (off by " + Math.abs(lineLength - indent) + ")");
		StringBuilder name = new StringBuilder();
		while(true){
			Character next = nextChar();
			if(next != null && next == ':')
				return Map.entry(name, nextValue());
			if(lineLength <= indent)
				throw new YayParsingException("newlines not allowed in object name");
			name.append(next);
			if(inner.isEmpty())
				throw new YayParsingException("missing ':' to end field name");
		}
	}
	
	public boolean equals(Object obj){
		return obj instanceof YayString ys
				&& ys.inner.equals(inner)
				&& ys.lineLength == lineLength
				&& ys.indent == indent;
	}
	
	public int hashCode(){
		return Objects.hash(inner, lineLength, indent);
	}
	
	public String toString(){
		return "yay[" + inner + "]@" + lineLength + "," + indent;
	}
	
	public static class YayParsingException extends RuntimeException{
		public YayParsingException(String message){
			super(message);
		}
	}
}