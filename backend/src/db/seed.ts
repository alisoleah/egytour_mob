import { db } from './index';
import { sites, pointsOfInterest, audioContent } from './schema';
import { sql } from 'drizzle-orm';

export async function seedGizaPyramids() {
    console.log('🌱 Seeding Giza Pyramids...');

    // Insert Giza site
    const [gizaSite] = await db.execute(sql`
    INSERT INTO sites (
      name_en, name_ar, name_fr,
      description_en, description_ar, description_fr,
      region, coordinates, opening_hours, entry_fee, image_url
    ) VALUES (
      'Giza Pyramid Complex',
      'مجمع أهرامات الجيزة',
      'Complexe pyramidal de Gizeh',
      'The Giza pyramid complex is an archaeological site on the Giza Plateau, on the outskirts of Cairo, Egypt. It includes the three Great Pyramids, the Great Sphinx, and several cemeteries.',
      'مجمع أهرامات الجيزة هو موقع أثري على هضبة الجيزة، على مشارف القاهرة، مصر. يضم الأهرامات الثلاثة الكبرى وأبو الهول العظيم والعديد من المقابر.',
      'Le complexe pyramidal de Gizeh est un site archéologique sur le plateau de Gizeh, à la périphérie du Caire, en Égypte.',
      'cairo',
      ST_GeogFromText('POINT(31.1342 29.9792)'),
      '{"monday": {"open": "08:00", "close": "17:00"}, "tuesday": {"open": "08:00", "close": "17:00"}, "wednesday": {"open": "08:00", "close": "17:00"}, "thursday": {"open": "08:00", "close": "17:00"}, "friday": {"open": "08:00", "close": "17:00"}, "saturday": {"open": "08:00", "close": "17:00"}, "sunday": {"open": "08:00", "close": "17:00"}}'::jsonb,
      '{"egyptian": 60, "foreign": 200, "student": 100, "currency": "EGP"}'::jsonb,
      'https://cdn.egytour.app/sites/giza-pyramids.jpg'
    )
    RETURNING id
  `);

    const siteId = gizaSite.rows[0].id;

    // Insert POIs
    const poisData = [
        {
            name_en: 'Great Pyramid of Khufu',
            name_ar: 'هرم خوفو الأكبر',
            name_fr: 'Grande pyramide de Khéops',
            poi_type: 'pyramid',
            lat: 29.9792,
            lon: 31.1342,
            radius: 30,
            period: 'old_kingdom',
            rank: 10,
            image: 'https://cdn.egytour.app/pois/great-pyramid.jpg'
        },
        {
            name_en: 'The Great Sphinx',
            name_ar: 'أبو الهول',
            name_fr: 'Le Grand Sphinx',
            poi_type: 'statue',
            lat: 29.9753,
            lon: 31.1376,
            radius: 25,
            period: 'old_kingdom',
            rank: 10,
            image: 'https://cdn.egytour.app/pois/sphinx.jpg'
        },
        {
            name_en: 'Pyramid of Khafre',
            name_ar: 'هرم خفرع',
            name_fr: 'Pyramide de Khéphren',
            poi_type: 'pyramid',
            lat: 29.9759,
            lon: 31.1308,
            radius: 30,
            period: 'old_kingdom',
            rank: 9,
            image: 'https://cdn.egytour.app/pois/khafre-pyramid.jpg'
        },
        {
            name_en: 'Pyramid of Menkaure',
            name_ar: 'هرم منقرع',
            name_fr: 'Pyramide de Mykérinos',
            poi_type: 'pyramid',
            lat: 29.9722,
            lon: 31.1281,
            radius: 25,
            period: 'old_kingdom',
            rank: 8,
            image: 'https://cdn.egytour.app/pois/menkaure-pyramid.jpg'
        },
        {
            name_en: 'Solar Boat Museum',
            name_ar: 'متحف مركب الشمس',
            name_fr: 'Musée de la barque solaire',
            poi_type: 'museum',
            lat: 29.9784,
            lon: 31.1351,
            radius: 20,
            period: 'old_kingdom',
            rank: 7,
            image: 'https://cdn.egytour.app/pois/solar-boat.jpg'
        }
    ];

    const insertedPOIs = [];
    for (const poi of poisData) {
        const [result] = await db.execute(sql`
      INSERT INTO points_of_interest (
        site_id, name_en, name_ar, name_fr, poi_type,
        coordinates, trigger_radius_meters, historical_period,
        importance_rank, image_url
      ) VALUES (
        ${siteId}, ${poi.name_en}, ${poi.name_ar}, ${poi.name_fr}, ${poi.poi_type},
        ST_GeogFromText(${`POINT(${poi.lon} ${poi.lat})`}),
        ${poi.radius}, ${poi.period}, ${poi.rank}, ${poi.image}
      )
      RETURNING id
    `);
        insertedPOIs.push(result.rows[0].id);
    }

    // Insert audio content for each POI (3 languages × 3 expertise levels)
    const languages = ['en', 'ar', 'fr'];
    const levels = ['casual', 'enthusiast', 'scholar'];
    const narrators = { en: 'James Wilson', ar: 'أحمد محمد', fr: 'Marie Dubois' };

    for (const poiId of insertedPOIs) {
        for (const lang of languages) {
            for (const level of levels) {
                const duration = level === 'casual' ? 120 : level === 'enthusiast' ? 240 : 360;
                const fileSize = level === 'casual' ? 2000000 : level === 'enthusiast' ? 4000000 : 6000000;

                await db.execute(sql`
          INSERT INTO audio_content (
            poi_id, language_code, expertise_level, audio_url,
            audio_duration_seconds, transcript, narrator_name, file_size_bytes
          ) VALUES (
            ${poiId}, ${lang}, ${level},
            ${`https://cdn.egytour.app/audio/${poiId}/${lang}/${level}.mp3`},
            ${duration},
            ${`[${level}] Audio transcript for POI in ${lang}`},
            ${narrators[lang as keyof typeof narrators]},
            ${fileSize}
          )
        `);
            }
        }
    }

    // Seed Knowledge Chunks for RAG
    console.log('📚 Seeding Egyptologist knowledge chunks for RAG...');
    const knowledgeData = [
        {
            title: 'Great Pyramid of Khufu Architecture & Workers',
            period: 'Old Kingdom (4th Dynasty, c. 2560 BCE)',
            category: 'architecture',
            content: 'The Great Pyramid of Giza was constructed under Pharaoh Khufu. It originally stood 146.6m tall and comprises 2.3 million stone blocks. Archaeological excavations prove it was built by well-fed Egyptian skilled craftsmen and workers, not enslaved people.'
        },
        {
            title: 'The Great Sphinx Iconography & Dream Stele',
            period: 'Old Kingdom (4th Dynasty, c. 2500 BCE)',
            category: 'mythology',
            content: 'Carved out of natural bedrock, the Sphinx portrays a lion with the portrait head of Pharaoh Khafre. The Dream Stele between its front paws chronicles Thutmose IV clearing the sands.'
        }
    ];

    for (const chunk of knowledgeData) {
        await db.execute(sql`
            INSERT INTO knowledge_chunks (
                title, content, historical_period, category, language_code
            ) VALUES (
                ${chunk.title}, ${chunk.content}, ${chunk.period}, ${chunk.category}, 'en'
            )
        `);
    }

    console.log('✅ Giza Pyramids & Knowledge Chunks seeded successfully!');
    console.log(`   - 1 site`);
    console.log(`   - ${poisData.length} POIs`);
    console.log(`   - ${poisData.length * 9} audio files`);
    console.log(`   - ${knowledgeData.length} knowledge chunks`);
}


// Run seed if called directly
if (require.main === module) {
    seedGizaPyramids()
        .then(() => {
            console.log('✅ Seeding complete!');
            process.exit(0);
        })
        .catch((error) => {
            console.error('❌ Seeding failed:', error);
            process.exit(1);
        });
}
