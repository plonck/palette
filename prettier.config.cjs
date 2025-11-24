module.exports = {
  plugins: [require.resolve('prettier-plugin-java')],

  quoteProps: 'consistent',
  singleQuote: true,
  trailingComma: 'all',
  printWidth: 80,

  overrides: [
    {
      files: ['*.java'],
      options: {
        parser: 'java',
      },
    },
  ],
};
