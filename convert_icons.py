
import os
from PIL import Image

def resize_and_save(src_path, dest_dir, sizes):
    if not os.path.exists(src_path):
        print(f"Source file not found: {src_path}")
        return

    try:
        with Image.open(src_path) as img:
            # Ensure transparency is preserved
            if img.mode != 'RGBA':
                img = img.convert('RGBA')

            for folder, size in sizes.items():
                target_dir = os.path.join(dest_dir, folder)
                if not os.path.exists(target_dir):
                    os.makedirs(target_dir)

                # ic_launcher.webp
                resized_img = img.resize((size, size), Image.Resampling.LANCZOS)
                target_path = os.path.join(target_dir, "ic_launcher.webp")
                resized_img.save(target_path, "WEBP")
                print(f"Saved {target_path}")

                # ic_launcher_round.webp (same usage for now, just circular masked if needed, but standard square with transparency is often fine for simple replacement)
                # Ideally we should crop to circle for round, but for now we just resize the transparent logo.
                target_path_round = os.path.join(target_dir, "ic_launcher_round.webp")
                resized_img.save(target_path_round, "WEBP")
                print(f"Saved {target_path_round}")
                
            # Handle Foreground for Adaptive Icons (drawable-anydpi-v26 usually references a foreground drawable)
            # We can create a simple ic_launcher_foreground.webp in drawable/ or mipmap-anydpi-v26/ ??
            # Actually standard practice is drawable/ic_launcher_foreground.xml or .png/.webp
            # Let's put a high-res version in drawable for the foreground
            drawable_dir = os.path.join(dest_dir, "drawable")
            if not os.path.exists(drawable_dir):
                 os.makedirs(drawable_dir)
            
            foreground_path = os.path.join(drawable_dir, "ic_launcher_foreground.webp")
            # 108x108 is the viewport, but often provided as 432x432 for full bleed. Let's use 432x432.
            foreground_img = img.resize((432, 432), Image.Resampling.LANCZOS)
            foreground_img.save(foreground_path, "WEBP")
            print(f"Saved {foreground_path}")

    except Exception as e:
        print(f"Error processing image: {e}")

if __name__ == "__main__":
    # Source image path provided by user
    source_image = r"C:\Users\Andrew\AndroidStudioProjects\Bootcamp\star-logo__3_-removebg-preview.webp"
    
    # Destination res directory
    res_directory = r"C:\Users\Andrew\AndroidStudioProjects\Bootcamp\app\src\main\res"

    # Android Mipmap sizes (standard)
    # mdpi: 48x48
    # hdpi: 72x72
    # xhdpi: 96x96
    # xxhdpi: 144x144
    # xxxhdpi: 192x192
    mipmap_sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192
    }

    resize_and_save(source_image, res_directory, mipmap_sizes)
