/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  docsSidebar: [
    'getting-started',
    'architecture',
    {
      type: 'category',
      label: 'Modules',
      collapsed: false,
      items: [
        'modules/rooms',
        'modules/reservations',
        'modules/restaurant',
        'modules/orders',
        'modules/billing',
        'modules/inventory',
      ],
    },
    'api-reference',
    'deployment',
    'data-generator',
  ],
};

export default sidebars;
