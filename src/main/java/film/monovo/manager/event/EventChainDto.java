package film.monovo.manager.event;

import java.util.List;

public class EventChainDto {
	public final long uid;
	public final List<Long> uids;
	public final boolean isDeleted;
	
	public EventChainDto(long uid, List<Long> uids, boolean isDeleted) {
		this.uid = uid;
		this.uids = uids;
		this.isDeleted = isDeleted;
	}
}
