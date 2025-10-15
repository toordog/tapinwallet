# Building xrpl.bundle.js for Browser Use

This guide explains how to bundle `xrpl.js` into a single JavaScript file that works in the browser with a `<script>` tag.

---

## 1. Create project
```bash
mkdir xrpl-browser
cd xrpl-browser
npm init -y
```

## 2. Install dependencies
```bash
npm install xrpl
npm install --save-dev rollup @rollup/plugin-node-resolve @rollup/plugin-commonjs @rollup/plugin-json
```

## 3. Make source file
**src/index.js**
```js
import * as xrpl from 'xrpl';
window.xrpl = xrpl;   // expose as global
```

## 4. Make Rollup config
**rollup.config.mjs**
```js
import resolve from '@rollup/plugin-node-resolve'
import commonjs from '@rollup/plugin-commonjs'
import json from '@rollup/plugin-json'

export default {
  input: 'src/index.js',
  output: {
    file: 'dist/xrpl.bundle.js',
    format: 'iife',
    name: 'xrpl',
  },
  plugins: [
    resolve({ browser: true, preferBuiltins: false }),
    commonjs(),
    json()
  ],
}
```

## 5. Update package.json
Add build script:
```json
"scripts": {
  "build": "rollup -c"
}
```

## 6. Run build
```bash
npm run build
```

✅ Output file: `dist/xrpl.bundle.js`

## 7. Use in XHTML / HTML
Reference the bundle in your app:
```html
<script src="dist/xrpl.bundle.js"></script>
<script>
  const client = new xrpl.Client("wss://s.altnet.rippletest.net:51233");
</script>
```

---

Repeat step 6 whenever you need to rebuild after upgrading `xrpl`.
