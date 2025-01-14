package com.example.huddle

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_policy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text = "    <p><strong>Huddle App</strong> (\"we,\" \"our,\" or \"us\") respects your privacy and is committed to protecting the personal information you share with us. This Privacy Policy outlines how we collect, use, and safeguard your data when you use the Huddle mobile application (\"the App\") to manage team and task activities. By using the App, you agree to the terms outlined in this policy.</p>\n" +
                "\n" +
                "    <h2>1. Information We Collect</h2>\n" +
                "    <p>We collect the following types of information:</p>\n" +
                "\n" +
                "    <h3>a. Personal Information</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Account Details:</strong> When you register or log in to the App using Firebase Authentication, we collect your email address, phone number (if applicable), and profile details such as your display name.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>b. Activity Data</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Tasks and Team Activities:</strong> Information about tasks, team members, and activities you create or manage within the App.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>c. Device Information</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Log Data:</strong> We may collect details about your interactions with the App, such as IP address, device type, operating system version, and time of access.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>d. Crash Reports</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Diagnostics:</strong> Through Firebase Crashlytics, we collect crash logs and diagnostics to identify and resolve technical issues in the App.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>e. Notification Data</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Push Notifications:</strong> We collect and process notification tokens to send team activity updates and task reminders.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>f. Permission Data</h3>\n" +
                "    <ul>\n" +
                "        <li><strong>Notification and Internet Permissions:</strong> The App requires notification and internet permissions to function effectively. These permissions allow us to send push notifications and facilitate real-time updates.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h2>2. How We Use Your Information</h2>\n" +
                "    <p>We use the information we collect for the following purposes:</p>\n" +
                "\n" +
                "    <h3>a. App Functionality</h3>\n" +
                "    <ul>\n" +
                "        <li>To authenticate users and maintain secure access to the App.</li>\n" +
                "        <li>To manage and display team activities and task details.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>b. Communication</h3>\n" +
                "    <ul>\n" +
                "        <li>To send important notifications about tasks, team updates, and reminders.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>c. Improvement and Optimization</h3>\n" +
                "    <ul>\n" +
                "        <li>To analyze usage patterns and improve the App’s features.</li>\n" +
                "        <li>To identify and fix bugs through crash reports and diagnostics.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>d. Legal Compliance</h3>\n" +
                "    <ul>\n" +
                "        <li>To comply with applicable laws, regulations, and legal processes.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h2>3. How We Share Your Information</h2>\n" +
                "    <p>We do not sell or rent your personal information. However, we may share your information in the following circumstances:</p>\n" +
                "\n" +
                "    <h3>a. Service Providers</h3>\n" +
                "    <ul>\n" +
                "        <li>We use Firebase services (Authentication, Firestore, Crashlytics, and Messaging) provided by Google to process and store data securely.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>b. Legal Requirements</h3>\n" +
                "    <ul>\n" +
                "        <li>We may disclose your information if required by law or to protect our rights, property, or safety.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h3>c. Business Transfers</h3>\n" +
                "    <ul>\n" +
                "        <li>In the event of a merger, acquisition, or sale of assets, your information may be transferred as part of that transaction.</li>\n" +
                "    </ul>\n" +
                "\n" +
                "    <h2>4. Data Retention</h2>\n" +
                "    <p>We retain your personal information for as long as necessary to provide the services offered by the App. If you delete your account, we will securely delete or anonymize your data within a reasonable time, except where we are required to retain it by law.</p>\n" +
                "\n" +
                "    <h2>5. Your Rights and Choices</h2>\n" +
                "\n" +
                "    <h3>a. Access and Update</h3>\n" +
                "    <p>You can view and update your profile information in the App.</p>\n" +
                "\n" +
                "    <h3>b. Data Deletion</h3>\n" +
                "    <p>You can request the deletion of your account and associated data by contacting us at huddle.apps@gmail.com</p>\n" +
                "\n" +
                "    <h3>c. Opt-Out of Notifications</h3>\n" +
                "    <p>You can opt out of receiving notifications by adjusting your device settings or disabling notifications in the App.</p>\n" +
                "\n" +
                "    <h2>6. Security</h2>\n" +
                "    <p>We use industry-standard security measures to protect your data. This includes encrypted communication between the App and Firebase services and secure storage of sensitive information. Despite these measures, no system is completely secure, and we cannot guarantee absolute security.</p>\n" +
                "\n" +
                "    <h2>7. Third-Party Services</h2>\n" +
                "    <p>The App uses Firebase, a Google service, to handle authentication, database storage, crash analytics, and messaging. Firebase services are governed by <a href=\"https://policies.google.com/privacy\" target=\"_blank\">Google's Privacy Policy</a>.</p>\n" +
                "\n" +
                "    <h2>8. Children’s Privacy</h2>\n" +
                "    <p>The App is not intended for use by individuals under the age of 13. We do not knowingly collect personal information from children. If we become aware that we have collected data from a child without parental consent, we will take steps to delete it.</p>\n" +
                "\n" +
                "    <h2>9. Changes to This Privacy Policy</h2>\n" +
                "    <p>We may update this Privacy Policy from time to time. Changes will be effective immediately upon posting the revised policy in the App. We encourage you to review this policy periodically.</p>\n" +
                "\n" +
                "    <h2>10. Contact Us</h2>\n" +
                "    <p>If you have questions or concerns about this Privacy Policy or our data practices, please contact us at:</p>\n" +
                "    <p><strong>Email:</strong> huddle.apps@gmail.com<br>\n" +
                "    <strong>Address:</strong> IIIT Allahabad, Jhalwa, Prayagraj, India</p>\n" +
                "\n" +
                "    <p>By using the Huddle App, you agree to this Privacy Policy. If you do not agree, please discontinue use of the App.</p>\n" +
                "</div>"

        val textView = findViewById<TextView>(R.id.privacy_tv)
        textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}