# Java Nexus Horizons multiplayer o'yini

## Loyiha tuzilishi
Loyiha bir nechta asosiy komponentlardan iborat:

- `NetworkHandler`: Ko'p o'yinchili funksionallik uchun server-mijoz aloqasini boshqaradi.
- `Bullet`: O'yindagi snaryad(o'qlar)larni ifodalaydi.
- `Enemy`: AI xatti-harakatlariga ega dushman ob'ektlarini belgilaydi.
- `Entity`: O'yin ob'ektlari uchun asosiy klass.

## Asosiy xususiyatlar

### Tarmoq (NetworkHandler.java)
- Ko'p o'yinchili o'yin uchun server-mijoz arxitekturasi.
- Aloqa uchun Java soketlaridan foydalanadi.
- O'yin holati ma'lumotlarini yuborish va qabul qilishni qo'llab-quvvatlaydi.

### O'yin ob'ektlari

#### O'q (Bullet.java)
- Tezlik va qarshilik bilan snaryad fizikasini amalga oshiradi.
- Dinamik uzunlik va rang bilan vizual tasvir.
- Ekran chegaralari bilan to'qnashuv aniqlash.

#### Dushman (Enemy.java)
- Maqsadni izlash xatti-harakati bilan AI boshqaradigan harakat.
- Vizual sog'liq paneli bilan sog'liq tizimi.
- Zarrachalar effekti bilan o'lim animatsiyasi.
- Uzluksiz harakat uchun ekranni o'rash.

#### Ob'ekt (Entity.java)
- O'yin ob'ektlari uchun asosiy klass.
- Pozitsiya va rang kabi umumiy xususiyatlarni boshqaradi.

## Boshlash

### Talablar
- Java Development Kit (JDK) 8 yoki undan yuqori versiya
- Java-ni qo'llab-quvvatlaydigan IDE (masalan, IntelliJ IDEA, Eclipse)

### O'yinni ishga tushirish
1. Repozitoriyni klonlash:
   ```
   git clone https://github.com/sizning-foydalanuvchi-ismingiz/sizning-repo-nomingiz.git
   ```
2. Loyihani IDE-da oching.
3. Asosiy o'yin klassini ishga tushiring (berilgan kod parchalarida ko'rsatilmagan).

## Contributing
Ushbu loyihaga hissa qo'shish uchun xush kelibsiz. Iltimos, quyidagi bosqichlarni bajaring:
1. Repozitoriyni fork qiling.
2. Yangi xususiyat uchun yangi branch yarating.
3. O'zgartirishlaringizni commit qiling.
4. Branch-ga push qiling.
5. Yangi Pull Request yarating.

## Acknowledgments
- Ushbu loyihada takroriy kodni kamaytirish uchun Lombok kutubxonasidan foydalanilgan.
- Ovoz effektlari uchun audio utilitalardan foydalanilgan (amalga oshirish berilgan kod parchalarida ko'rsatilmagan).
