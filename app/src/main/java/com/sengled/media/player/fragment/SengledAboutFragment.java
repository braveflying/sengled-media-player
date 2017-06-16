package com.sengled.media.player.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.sengled.media.player.R;

/**
 * Created by admin on 2017/5/11.
 */
public class SengledAboutFragment extends MaterialAboutFragment {
    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("Sengled Media Player")
                .icon(R.mipmap.sengled_default_photo)
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(context, Uri.parse("http://www.sengled.com")))
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Version")
                .subText("1.0.0")
                .icon(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_info_outline)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Licenses")
                .icon(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_book)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .build());

        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title("Author");

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.company_name)
                .subText("Beijing China")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_home)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Phone")
                .subText("010-5625152")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_phone)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createPhoneOnClickAction(context,"010-5625152"))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("QQ")
                .subText("105625824")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_qqchat)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Email")
                .subText("105625824@sengled.com")
                .icon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(ContextCompat.getColor(context, R.color.colorPrimary))
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createEmailOnClickAction(context, "105625824@sengled.com","send email"))
                .build());

        return new MaterialAboutList.Builder()
                .addCard(appCardBuilder.build())
                .addCard(authorCardBuilder.build())
                .build();
    }
}
