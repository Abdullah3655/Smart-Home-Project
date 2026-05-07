"""Convert a markdown file to a styled standalone HTML ready for browser
print-to-PDF.

Usage:  python md2html.py <input.md> <output.html>
"""
import sys
import pathlib
import markdown

CSS = """
@page { size: A4; margin: 18mm; }
body {
  font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  font-size: 11pt;
  line-height: 1.45;
  color: #1f2940;
  max-width: 920px;
  margin: 24px auto;
  padding: 0 24px;
}
h1 { font-size: 22pt; border-bottom: 2px solid #4a73c5; padding-bottom: 6px; }
h2 { font-size: 16pt; color: #2a4d8f; margin-top: 28px; }
h3 { font-size: 13pt; color: #2a4d8f; }
h4 { font-size: 12pt; color: #4a5a70; }
p { margin: 0.6em 0; }
code {
  background: #f4f6f9;
  padding: 1px 5px;
  border-radius: 3px;
  font-family: 'Consolas', 'Courier New', monospace;
  font-size: 10pt;
}
pre {
  background: #f4f6f9;
  padding: 10px 14px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 9.5pt;
}
table {
  border-collapse: collapse;
  margin: 0.8em 0;
  width: 100%;
}
table, th, td {
  border: 1px solid #c7d2e3;
}
th, td { padding: 6px 10px; text-align: left; vertical-align: top; }
th { background: #eaf0fb; }
img { max-width: 100%; height: auto; }
blockquote {
  border-left: 4px solid #c7d2e3;
  margin: 0.8em 0;
  padding: 6px 14px;
  color: #5a6478;
  background: #f8f9fb;
}
ul, ol { padding-left: 1.6em; }
li { margin: 0.2em 0; }
hr { border: none; border-top: 1px solid #c7d2e3; margin: 1.6em 0; }
"""

TEMPLATE = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8"/>
<title>{title}</title>
<style>{css}</style>
</head>
<body>
{body}
</body>
</html>
"""

def main(src: str, dst: str) -> None:
    src_path = pathlib.Path(src)
    md_text = src_path.read_text(encoding="utf-8")
    body = markdown.markdown(
        md_text,
        extensions=["tables", "fenced_code", "toc", "md_in_html", "attr_list"],
    )
    title = src_path.stem
    html = TEMPLATE.format(title=title, css=CSS, body=body)
    pathlib.Path(dst).write_text(html, encoding="utf-8")
    print(f"Wrote {dst} ({len(html):,} bytes)")

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
