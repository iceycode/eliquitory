#Assets#

Info about assets here. They are in android module, in assets directory.

##Fonts##
----

Libgdx uses BitmapFont format, so need to convert using Hiero.jar (in gdx-tools) or BMFont.

Currently using roboto.

<b>Font Paths:</b>

roboto:

    fonts/roboto/roboto-bold16.fnt
    fonts/roboto/roboto-bold16.png
    fonts/roboto/roboto-light14.fnt
    fonts/roboto/roboto-light14.png
    fonts/roboto/roboto-light16.fnt
    fonts/roboto/roboto-light16.png
    fonts/roboto/roboto-reg14.fnt
    fonts/roboto/roboto-reg14.png
    fonts/roboto/roboto-reg14_0.tga
    fonts/roboto/roboto-reg16.fnt
    fonts/roboto/roboto-reg16_0.tga
    fonts/roboto/roboto-reg20.fnt
    fonts/roboto/roboto-reg20.png

roboto-condensed:

    fonts/roboto-condensed/roboto-condensed20.fnt
    fonts/roboto-condensed/roboto-condensed20.png

roboto-slap:

    fonts/roboto-slap/roboto-slap12.fnt
    fonts/roboto-slap/roboto-slap12_0.tga
    fonts/roboto-slap/roboto-slap-light12.fnt
    fonts/roboto-slap/roboto-slap-light12_0.tga
    fonts/roboto-slap/roboto-slap-light14.fnt
    fonts/roboto-slap/roboto-slap-light14_0.tga

open-sans:

    fonts/open-sans/opensans_bold_med.fnt
    fonts/open-sans/opensans_bold_med_0.tga
    fonts/open-sans/opensans_bold_reg.fnt
    fonts/open-sans/opensans_bold_reg_0.tga
    fonts/open-sans/opensans_light_med.fnt
    fonts/open-sans/opensans_light_med_0.tga

roboto-condensed

    fonts/roboto-condensed/roboto-condensed20.fnt

##Colors##
----

For the theme, I used Adobe Photoshop to create the backgrounds, buttons, labels, select boxes, sliders and scroll bars.

To create a color, with libgdx colors, add it the skin.
This is how to figure out color code values:

- Figure out values using RGB color codes using this online [resource](http://www.rapidtables.com/web/color/RGB_Color.htm).
- Pick the color and note the Red, Green, Blue values
- for those values, divide by 255 and add that to the Color entry in skin.json

<b>Main colors:</b>
White - for most elements requiring fonts
Red - for dialog tabs and supply amounts that are below 5 ml
Green - for supply amounts over 15 ml

<b>Custom colors:</b>
Bluish - Cyan? (don't know official name):

    Hex#: 66B2FF    Red: 102   Green: 178    Blue: 255
    com.badlogic.gdx.graphics.Color: {
        ...
        cyan: {a: 1, b: 1, g: .7, r: .4}
    }


##Themes##
----

Current theme is dark-blue. See folder assets/skins/dark & assets/textures/theme-dark

In future, I may also add more themes, such as a light theme. However, since I am not a graphics designer, it
took me a good amount of time to create a reasonably good-looking theme.

Also, as a note to myself, it is FAR more efficient to use just one skin for all the screens. This is beneficial
in terms of ease of implementation and time spent on creating the skins.

I mainly used Photoshop and created most of the theme myself, but see credits for items I used to create certain
graphical elements of the app (icon and some of the UI).

##Credits##
----

These are credits for various assets either used in when creating the grpahical elements of
the app or simply downloaded as a potential template/object to use.



###Drawable###

In use means that they are being used during run time.
NOTE: there may be assets that are currently not in use that are in android/assets.

Drawable assets: icons, textures, etc.

| In use | File Name 		| Asset Type | Source |
|:---------:|:---------------:|:------:|:-------:|
| No | TestTube-50.png | Icon |http://icons8.com/web-app/847/Test-Tube | 
| Yes | beaker5.svg | Icon | <div>Icon made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div> |
| Yes | eye34.svg | Icon | http://www.freepik.com (same as above) |
| Yes | smoke01.ai | Vector | <a href="http://www.freepik.com/free-photos-vectors/background">Background vector designed by Freepik</a> |
| No | Smoke-vector-art.zip | Vector | <a href="http://www.freepik.com/free-photos-vectors/background">Background vector designed by Freepik |
| No | flask7.psd | Icon | http://www.freepik.com |


Might use Android Holo theme found on Android Developer [site](https://developer.android.com/design/downloads/index.html)

- I altered a couple of UI elements found in Android Holo Theme
- I used this online [tool](http://android-holo-colors.com/)
    - downloaded a zip containing individual pngs
    - chose the elements to use, and edited the pngs I used (see below)
    - Those used:
    1) Slider track
    2) Scroll track
    3) Textfields


