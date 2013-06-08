package tk.sweetvvck.lecture;

/**
 * Lecture entity. @author MyEclipse Persistence Tools
 */

public class Lecture implements java.io.Serializable {

	private static final long serialVersionUID = -2997275044971667548L;
	private String address;
	private String speakerinfo;
	private String host;
	private String content;
	private String lecture;
	private String note;
	private String date;
    private String speaker;

	// Constructors

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}

	/** default constructor */
	public Lecture() {
	}

	/** minimal constructor */
	
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSpeakerinfo() {
		return this.speakerinfo;
	}

	public void setSpeakerinfo(String speakerinfo) {
		this.speakerinfo = speakerinfo;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLecture() {
		return this.lecture;
	}

	public void setLecture(String lecture) {
		this.lecture = lecture;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}