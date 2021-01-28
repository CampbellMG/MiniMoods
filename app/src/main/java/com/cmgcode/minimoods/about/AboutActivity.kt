package com.cmgcode.minimoods.about

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cmgcode.minimoods.R
import com.cmgcode.minimoods.databinding.ActivityAboutBinding
import com.cmgcode.minimoods.util.Constants.EMAIL
import com.cmgcode.minimoods.util.Constants.MAIL_TO
import com.cmgcode.minimoods.util.Constants.REPO
import com.cmgcode.minimoods.util.Constants.WEBSITE
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initialiseView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun initialiseView() {
        title = null

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        binding.contact.setOnClickListener { startEmailIntent() }

        binding.website.setOnClickListener { startWebsiteIntent(WEBSITE) }

        binding.contribute.setOnClickListener { startWebsiteIntent(REPO) }

        binding.appIcon.setOnLongClickListener { showConfetti(it) }

    }

    private fun startEmailIntent() {
        val title = getString(R.string.query)
        val uri = Uri.parse(MAIL_TO)

        val emailIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, title)
        }

        startActivity(Intent.createChooser(emailIntent, title))
    }

    private fun startWebsiteIntent(website: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
        startActivity(intent)
    }

    private fun showConfetti(it: View): Boolean {
        val location = IntArray(2)

        it.getLocationInWindow(location)

        binding.confetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.RED)
            .setDirection(0.0, 360.0)
            .setSpeed(7f, 20f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(location[0].toFloat() + it.width / 2, location[1].toFloat())
            .burst(100)

        return true
    }

}
