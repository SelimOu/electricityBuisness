const fs = require('fs');
const postcss = require('postcss');
const tailwind = require('tailwindcss');
const autoprefixer = require('autoprefixer');
const input = 'src/styles.css';
const css = fs.readFileSync(input, 'utf8');
postcss([tailwind, autoprefixer])
    .process(css, { from: input })
    .then(result => {
        fs.writeFileSync('test-output.css', result.css);
        console.log('OK: wrote test-output.css');
    })
    .catch(err => {
        console.error('ERROR:', err.stack || err.message || err);
        process.exit(1);
    });
