// @ts-check
import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Aetheria Resort',
  tagline: 'Hotel & Restaurant Management System — Spring Boot 4.1 + React + GraalVM',
  favicon: 'img/favicon.ico',

  future: {
    v4: true,
  },

  // GitHub Pages deployment configuration
  url: 'https://vasanthtcs2009.github.io',
  baseUrl: '/hotel-mgmt-graalvm/',

  organizationName: 'vasanthtcs2009',
  projectName: 'hotel-mgmt-graalvm',
  deploymentBranch: 'gh-pages',
  trailingSlash: false,

  onBrokenLinks: 'throw',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  markdown: {
    mermaid: true,
  },

  themes: ['@docusaurus/theme-mermaid'],

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: './sidebars.js',
          editUrl:
            'https://github.com/vasanthtcs2009/hotel-mgmt-graalvm/tree/main/docs/',
        },
        blog: false,
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      image: 'img/social-card.png',
      colorMode: {
        defaultMode: 'dark',
        respectPrefersColorScheme: true,
      },
      navbar: {
        title: 'Aetheria Resort',
        logo: {
          alt: 'Aetheria Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'docsSidebar',
            position: 'left',
            label: 'Documentation',
          },
          {
            href: 'https://github.com/vasanthtcs2009/hotel-mgmt-graalvm',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Documentation',
            items: [
              { label: 'Getting Started', to: '/docs/getting-started' },
              { label: 'Architecture', to: '/docs/architecture' },
              { label: 'API Reference', to: '/docs/api-reference' },
            ],
          },
          {
            title: 'Modules',
            items: [
              { label: 'Hotel Rooms', to: '/docs/modules/rooms' },
              { label: 'Reservations', to: '/docs/modules/reservations' },
              { label: 'Restaurant', to: '/docs/modules/restaurant' },
              { label: 'Inventory', to: '/docs/modules/inventory' },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/vasanthtcs2009/hotel-mgmt-graalvm',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Aetheria Resort — Built with Docusaurus.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ['java', 'bash', 'json', 'yaml', 'sql'],
      },
      mermaid: {
        theme: { light: 'neutral', dark: 'dark' },
      },
    }),
};

export default config;
