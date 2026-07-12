import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import styles from './index.module.css';

function HeroBanner() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero', styles.heroBanner)}>
      <div className="container">
        <h1 className={styles.heroTitle}>
          <span className={styles.heroGradient}>Aetheria Resort</span>
        </h1>
        <p className={styles.heroSubtitle}>{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--primary button--lg"
            to="/docs/getting-started">
            📖 Get Started
          </Link>
          <Link
            className="button button--secondary button--lg"
            to="/docs/architecture">
            🏛️ Architecture
          </Link>
          <Link
            className="button button--secondary button--lg"
            to="/docs/api-reference">
            🔌 API Reference
          </Link>
        </div>
      </div>
    </header>
  );
}

const features = [
  {
    title: '⚡ GraalVM Native',
    description: 'Compiled to native binary for ~50ms startup and 80MB memory footprint. No JVM overhead.',
  },
  {
    title: '🏨 Hotel Operations',
    description: 'Room management, reservation lifecycle, availability checks with Redis caching.',
  },
  {
    title: '🍽️ Restaurant & Kitchen',
    description: 'Full menu management, kitchen order tracking, and automatic ingredient deductions.',
  },
  {
    title: '💰 Unified Billing',
    description: 'Checkout invoices combining room charges with all restaurant orders, including tax computation.',
  },
  {
    title: '📦 Inventory Tracking',
    description: 'Real-time stock levels, atomic deductions on orders, and reorder alerts.',
  },
  {
    title: '🚀 100+ TPS Benchmark',
    description: 'Batch data generator seeding millions of records with optimized JDBC batch inserts.',
  },
];

function FeatureCard({title, description}) {
  return (
    <div className={clsx('col col--4', styles.featureCol)}>
      <div className={styles.featureCard}>
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

function TechStack() {
  const techs = [
    { label: 'Java 25', icon: '☕' },
    { label: 'Spring Boot 4.1', icon: '🍃' },
    { label: 'React 19', icon: '⚛️' },
    { label: 'PostgreSQL 16', icon: '🐘' },
    { label: 'Redis 7', icon: '🔴' },
    { label: 'GraalVM', icon: '🔥' },
    { label: 'Docker', icon: '🐳' },
    { label: 'Vite 8', icon: '⚡' },
  ];

  return (
    <section className={styles.techSection}>
      <div className="container">
        <h2 className={styles.sectionTitle}>Technology Stack</h2>
        <div className={styles.techGrid}>
          {techs.map((tech) => (
            <div key={tech.label} className={styles.techBadge}>
              <span className={styles.techIcon}>{tech.icon}</span>
              <span>{tech.label}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={siteConfig.title}
      description="Hotel & Restaurant Management System documentation — Spring Boot 4.1, React, GraalVM Native Image">
      <HeroBanner />
      <main>
        <section className={styles.features}>
          <div className="container">
            <div className="row">
              {features.map((props, idx) => (
                <FeatureCard key={idx} {...props} />
              ))}
            </div>
          </div>
        </section>
        <TechStack />
      </main>
    </Layout>
  );
}
