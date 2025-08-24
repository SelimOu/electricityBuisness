const fs = require('fs');
const path = require('path');
const glob = require('glob');
const files = glob.sync('src/app/**/*.css');
let changed = [];
for (const f of files) {
    const content = fs.readFileSync(f, 'utf8');
    if (/var\s+resource;|\/\*\*\*\*\*\*\/ \(\) =>|webpackBootstrap/.test(content)) {
        fs.writeFileSync(f, ':host{display:block}\n');
        changed.push(f);
    }
}
console.log('Sanitized', changed.length, 'files');
if (changed.length) changed.forEach(x => console.log(' -', x));
