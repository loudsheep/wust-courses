package dsaa.lab02;

public class Link{
	public String ref;
	public Link(String ref) {
		this.ref=ref;
	}
	// in the future there will be more fields


	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Link))
			return false;

		return this.ref.equals(((Link) obj).ref);
	}

}
