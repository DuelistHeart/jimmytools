# Character Select

A fighting-game style roster that replaces the chest menu `/char` opens. It is **off by default**; turn it on under
*Config → CharacterSelect → Enable Custom Character Select*.

It only applies when you are connected to `lords.rawb.tv`; anywhere else you get the server's chest as usual.

Your characters line up as a strip of faces along the bottom of the screen. Hover one to put them on the stage, with
their skin, name and the server's hints for them. Everything works the same as in the chest:

* **Left-click** a character to pick them.
* ++f++ (your *Swap Item With Offhand* key) begins character deletion.
* ++esc++ closes the screen.

## Character reveal

Picking a character plays a short "**JOINS THE BATTLE!**" reveal, which carries on while your character loads in.
Click, or press ++esc++ or ++enter++, to skip to the end.

The reveal can be turned off in the [config](../configuration.md#characterselect). The screen then just slides away
when you pick someone.

!!! info "How it works"
    The roster is drawn over the server's own menu, so your clicks reach the server exactly as they would from the
    chest. Your pick is sent straight away; the reveal is only for show and never delays it.
