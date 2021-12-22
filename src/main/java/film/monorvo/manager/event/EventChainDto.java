package film.monorvo.manager.event;

import java.util.List;

public class EventChainDto {
	public final long uid;
	public final List<Long> uids;
	public final boolean isDeleted;
	public final boolean isImported;
	
	public EventChainDto(long uid, List<Long> uids, boolean isDeleted, boolean isImported) {
		this.uid = uid;
		this.uids = uids;
		this.isDeleted = isDeleted;
		this.isImported = isImported;
	}
}
