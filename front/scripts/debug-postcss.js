const fs = require('fs');
const input = 'src/styles.css';
const css = fs.readFileSync(input, 'utf8');
console.log('--- first 200 chars ---');
console.log(css.slice(0, 200));
const lines = css.split(/\r?\n/);
console.log('\n--- lines 18..26 ---');
for (let i = 17; i <= 25 && i < lines.length; i++) console.log((i + 1) + ': ' + lines[i]);
