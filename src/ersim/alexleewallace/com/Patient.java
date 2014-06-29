package ersim.alexleewallace.com;

import java.util.ArrayList;

public class Patient {
	int id;
	ArrayList<ResponsePair> info;
	
	public Patient() {
		id = 0;
		info = new ArrayList<ResponsePair>();
	}
	
	public String getResponse(String query) {
		int i = 0;
		while (i < info.size() && !info.get(i).trigger.equals(query))
			i++;
		
		String response = "I don't know how to answer that.";
		if (i < info.size())
			response = info.get(i).response;
		return response;
	}
	
	public void addResponsePair(String trigger, String response, String media_file) {
		ResponsePair rp = new ResponsePair(trigger, response, media_file);
		info.add(rp);
	}
	
	public class ResponsePair {
		String trigger;
		String response;
		String media_file;
		
		public ResponsePair(String trigger, String response) {
			this.trigger = trigger;
			this.response = response;
			this.media_file = null;
		}
		
		public ResponsePair(String trigger, String response, String media_file) {
			this.trigger = trigger;
			this.response = response;
			this.media_file = media_file;
		}
	}
}
