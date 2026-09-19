# Building from Source

Requires JDK 25 or newer.

```bash
git clone https://github.com/DuelistHeart/jimmytools.git
cd jimmytools
./gradlew build
```

The built jar is in `build/libs/`. To run a development client:

```bash
./gradlew runClient
```

## Working on the wiki

The wiki is built with [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) from the `docs/` folder.

```bash
pip install -r requirements-docs.txt
mkdocs serve      # live preview at http://127.0.0.1:8000
```

Pushes to `main` that touch `docs/` are published to GitHub Pages automatically.
