def recommend_tracks(user_history, catalog):
    preferred_genres = {}

    for track in user_history:
        genre = track["genre"]
        preferred_genres[genre] = preferred_genres.get(genre, 0) + 1

    top_genre = max(preferred_genres, key=preferred_genres.get)

    recommendations = [
        track for track in catalog
        if track["genre"] == top_genre
        and track["id"] not in [item["id"] for item in user_history]
    ]

    return recommendations[:10]