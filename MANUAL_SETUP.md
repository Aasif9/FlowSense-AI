# FlowSense-AI Manual Setup Instructions

## 📁 Required Fonts (Manrope & Inter)

You need to add these font files to `app/src/main/res/font/`:

### Download from Google Fonts:
1. **Manrope Family** (https://fonts.google.com/specimen/Manrope)
   - `manrope_bold.ttf`
   - `manrope_semibold.ttf` 
   - `manrope_regular.ttf`

2. **Inter Family** (https://fonts.google.com/specimen/Inter)
   - `inter_regular.ttf`
   - `inter_medium.ttf`
   - `inter_semibold.ttf`

### Steps:
1. Create folder: `app/src/main/res/font/`
2. Download the 6 font files above
3. Place them in the font folder
4. Clean and rebuild project

## 🎨 Required Category Icons

You need these vector drawable icons in `app/src/main/res/drawable/`:

### Download from Material Symbols:
1. **Food**: `ic_category_food.xml` (search "restaurant" or "food")
2. **Transport**: `ic_category_transport.xml` (search "directions_car" or "commute")
3. **Shopping**: `ic_category_shopping.xml` (search "shopping_bag" or "shopping_cart")
4. **Bills**: `ic_category_bills.xml` (search "receipt" or "description")
5. **Health**: `ic_category_health.xml` (search "medical_services" or "health_and_safety")
6. **Entertainment**: `ic_category_entertainment.xml` (search "movie" or "theater_comedy")
7. **Income**: `ic_category_income.xml` (search "trending_up" or "payments")
8. **Other**: `ic_category_other.xml` (search "more_horiz" or "category")

### Steps:
1. Go to https://fonts.google.com/icons
2. Search for each icon name above
3. Select the icon, choose "XML" format
4. Download and place in `app/src/main/res/drawable/`
5. Make sure filenames match exactly as listed above

## 🔧 Build Dependencies

Make sure your `app/build.gradle.kts` includes:
```kotlin
// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Hilt
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
```

## 🚀 After Setup

Once you've added the fonts and icons:
1. **Clean Project**: `Build → Clean Project`
2. **Rebuild**: `Build → Rebuild Project`
3. **Run App**: The dashboard should now display with proper fonts and icons

## 🐛 Common Issues

### Font Not Found:
- Verify font filenames match exactly in `Type.kt`
- Ensure fonts are in `res/font/` folder
- Clean and rebuild project

### Icon Not Found:
- Verify drawable filenames match exactly in `DashboardViewModel.kt`
- Ensure icons are in `res/drawable/` folder
- Check XML syntax in drawable files

### Build Errors:
- Make sure all dependencies are added to `build.gradle.kts`
- Check for missing imports in created files
- Verify Hilt annotation processing is enabled

## 📱 Expected Result

After setup, you should see:
- Beautiful dashboard with Manrope/Inter typography
- Category icons in expense breakdown
- Bottom navigation with proper icons
- Smooth animations and proper color scheme

The app will be ready for expense tracking with automatic UPI notification detection!
