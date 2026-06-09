def select_playback_node(nodes):
    available_nodes = [
        node for node in nodes
        if node["status"] == "available"
    ]

    if not available_nodes:
        raise Exception("No available playback nodes")

    return min(
        available_nodes,
        key=lambda node: node["latency"] + node["load"] * 0.5
    )