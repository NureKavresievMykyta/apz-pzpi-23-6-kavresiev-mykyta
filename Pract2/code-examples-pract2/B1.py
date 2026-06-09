from datetime import datetime

def create_user_event(user_id, event_type, track_id=None, query=None):
    event = {
        "user_id": user_id,
        "event_type": event_type,
        "track_id": track_id,
        "query": query,
        "created_at": datetime.utcnow().isoformat()
    }
    return event

event = create_user_event(
    user_id=125,
    event_type="play",
    track_id="track_4821"
)