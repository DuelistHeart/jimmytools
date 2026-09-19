# Custom Tab List

A scroll-styled replacement for the vanilla tab list. It is **off by default**; turn it on under
*Config → CustomTab → Enable Custom Tablist*.

Hold the Player List key (++tab++ by default) to unroll the scrolls:

* **Server** (green) – online players, with the server's player count, TPS and your ping.
* **Nearby** (teal) – characters near you, shown with their character names and skins.
* **District** – the district and plot you are standing in, and the plot owner, if any.

The scrolls scale down automatically on small windows or high GUI scales so they always fit on screen.

The open and close speed can be adjusted in the [config](../configuration.md#customtab).

!!! info "How it works"
    The tab list only reads information the server already sends to your client. It does not send any extra requests.
