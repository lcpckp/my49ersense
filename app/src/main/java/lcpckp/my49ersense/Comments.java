package lcpckp.my49ersense;

public class Comments {

	private String username,comment, dateTime;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	 // String representation
    public String toString() {
            return this.username + "        \t     \t \t         \t         on " + this.dateTime.substring(0, (this.dateTime.length()-12))+"\n\n"+this.comment;
    }
	
}
