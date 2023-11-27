package com.example.medicare.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.medicare.R
import com.example.medicare.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {
    companion object{
        private const val Metric_Units_View = " Metric_Units_View"
        private const val Us_Units_View = " Us_Units_View"

    }
    private var currentvisibleview:String = Metric_Units_View


    private var binding: ActivityBmiactivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setSupportActionBar(binding?.toolbarBmiActivity)
        setContentView(binding?.root)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATOR BMI"

        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        makevisiblemetricunitview()

        binding?.rgUnits?.setOnCheckedChangeListener{_,checkedID:Int ->
            if(checkedID == R.id.rbMetricUnits){
                makevisiblemetricunitview()
            }
            else{
                makevisibleUSunitview()
            }
        }

        binding?.btncalculator?.setOnClickListener {
            calculateUnits()
        }
    }
    private fun makevisiblemetricunitview(){
        currentvisibleview = Metric_Units_View
        binding?.tilmetricheight?.visibility = View.VISIBLE
        binding?.tilmetricweight?.visibility = View.VISIBLE

        binding?.tilUsMetricUnitWeight?.visibility = View.GONE
        binding?.tilmetricunitheightFeet?.visibility=View.GONE
        binding?.tilmetricunitheightInch?.visibility=View.GONE

        binding?.etMetricheight?.text!!.clear()
        binding?.etMetricheight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }
    private fun makevisibleUSunitview(){
        currentvisibleview = Us_Units_View
        binding?.tilmetricheight?.visibility = View.INVISIBLE
        binding?.tilmetricweight?.visibility = View.INVISIBLE

        binding?.tilUsMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilmetricunitheightFeet?.visibility=View.VISIBLE
        binding?.tilmetricunitheightInch?.visibility=View.VISIBLE

        binding?.etUSmetricheightfeet?.text!!.clear()
        binding?.etUSmetricheightinch?.text!!.clear()

        binding?.etUsMetricUnitWeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }


    private fun displayBMIResult(bmi:Float){
        val bmiLabel : String
        val bmiDescription: String
        var maletips: String = ""
        var femaletips: String = ""
        if(bmi.compareTo(15f)<=0){
            bmiLabel = "Very Severely UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
            maletips = "1. Consult a Healthcare Professional: Schedule a visit with a healthcare provider, such as a doctor or a registered dietitian, to determine the underlying causes of being underweight and receive personalized guidance.\n" +
                    "\n" +
                    "2. Increase Caloric Intake: Consume more calories than your body burns in a day. Choose nutrient-dense, high-calorie foods like nuts, seeds, avocados, and healthy oils to boost your caloric intake.\n" +
                    "\n" +
                    "3. Balanced Diet: Focus on a balanced diet that includes a variety of food groups. Incorporate lean proteins, complex carbohydrates, fruits, vegetables, and dairy or dairy alternatives.\n" +
                    "\n" +
                    "4. Strength Training: Start a strength training program to build muscle mass. Muscle weighs more than fat, so increasing muscle can contribute to healthy weight gain.\n" +
                    "\n" +
                    "5. Frequent, Smaller Meals: Eat more frequent meals and snacks throughout the day to increase calorie consumption. Aim for five to six meals or snacks to avoid feeling too full at once."
            femaletips = "1. Seek Professional Guidance: Consult with a healthcare professional or registered dietitian to address the underlying causes of being underweight and create a personalized plan for healthy weight gain.\n" +
                    "\n" +
                    "2. Caloric Increase: Increase your daily caloric intake by consuming nutrient-dense, high-calorie foods like nuts, seeds, and healthy fats. It's crucial to maintain a balanced diet while increasing calories.\n" +
                    "\n" +
                    "3. Protein-Rich Diet: Include ample protein sources in your diet, such as lean meats, poultry, fish, legumes, and dairy or dairy alternatives. Protein supports muscle growth.\n" +
                    "\n" +
                    "4. Strength Training and Exercise: Engage in strength training and resistance exercises to build muscle mass and improve overall health. A combination of cardiovascular exercise and strength training is ideal.\n" +
                    "\n" +
                    "5. Nutrient Supplementation: In some cases, your healthcare provider may recommend supplements like protein shakes or meal replacement drinks to help meet your calorie and nutrient needs."
        }else if(bmi.compareTo(15f)>0 && bmi.compareTo(16f)<=0){
            bmiLabel = "Severely UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
            maletips = "1. Consult a Healthcare Professional: It's crucial to consult with a healthcare provider or registered dietitian who can assess your specific situation, identify any underlying causes of severe underweight, and create a tailored plan for safe and effective weight gain.\n" +
                    "\n" +
                    "2. Caloric Surplus: To gain weight, you need to consume more calories than your body burns in a day. Focus on increasing your daily caloric intake by adding nutrient-dense foods to your diet.\n" +
                    "\n" +
                    "3. Balanced Diet: While aiming to eat more, maintain a balanced diet that includes a variety of food groups. Incorporate lean proteins, complex carbohydrates, healthy fats, fruits, vegetables, and dairy or dairy alternatives.\n" +
                    "\n" +
                    "4. Regular, Nutrient-Rich Meals: Eat frequent, well-balanced meals and snacks throughout the day. Include protein-rich foods such as lean meats, fish, legumes, and dairy. Whole grains and healthy fats are also essential.\n" +
                    "\n" +
                    "5. Strength Training: Engage in a strength training program to build muscle mass. Gaining muscle is a healthy way to increase body weight and improve overall health. Combine strength training with cardiovascular exercises for a well-rounded fitness routine."
            femaletips = "1. Consult with a Healthcare Professional: Before making any significant dietary changes, consult with a healthcare provider or registered dietitian. They can help assess your specific needs and create a personalized plan tailored to your health and goals.\n" +
                    "\n" +
                    "2. Increase Caloric Intake: To gain weight, you need to consume more calories than your body burns in a day. Focus on consuming nutrient-dense, calorie-rich foods like nuts, seeds, avocados, and whole grains.\n" +
                    "\n" +
                    "3. Balanced Diet: While increasing calorie intake, maintain a balanced diet that includes a variety of food groups. Prioritize whole foods, lean proteins, complex carbohydrates, healthy fats, fruits, vegetables, and dairy or dairy alternatives.\n" +
                    "\n" +
                    "4. Regular, Nutrient-Rich Meals: Eat frequent, well-balanced meals and snacks throughout the day. Include protein-rich foods like lean meats, fish, legumes, and dairy. Whole grains and healthy fats are also important components of a healthy diet.\n" +
                    "\n" +
                    "5. Strength Training: Incorporate a strength training program into your fitness routine to build muscle mass. Gaining muscle in a controlled and healthy manner can help increase body weight and improve overall health."
        }
        else if(bmi.compareTo(16f)>0 && bmi.compareTo(18.5f)<=0){
            bmiLabel = "UnderWeight"
            bmiDescription = "Oops! You really Need to take better care of yourself! Eat more!"
            maletips = "1. Consult a Healthcare Professional: Seek guidance from a doctor or a registered dietitian to determine your specific nutritional and health needs. They can help create a personalized plan for healthy weight gain.\n" +
                    "\n" +
                    "2. Increase Caloric Intake: Consume more calories than your body burns. Choose nutrient-dense, calorie-rich foods such as lean proteins, whole grains, healthy fats, and complex carbohydrates.\n" +
                    "\n" +
                    "3. Balanced Diet: Ensure a well-rounded diet that includes a variety of food groups. Prioritize lean meats, poultry, fish, legumes, whole-grain products, dairy or dairy alternatives, and plenty of fruits and vegetables.\n" +
                    "\n" +
                    "4. Regular, Nutrient-Rich Meals: Eat frequent meals and snacks throughout the day to meet your caloric goals. Include protein-rich foods to support muscle development and overall health.\n" +
                    "\n" +
                    "5. Strength Training: Incorporate strength training exercises into your fitness routine to build muscle mass. Gaining muscle can contribute to healthy weight gain and improved overall fitness."
            femaletips = "1. Consult a Healthcare Professional: Start by consulting a healthcare provider or a registered dietitian to assess your specific nutritional requirements and create a personalized plan for healthy weight gain.\n" +
                    "\n" +
                    "2. Increase Caloric Intake: Consume more calories than your body burns, focusing on nutrient-dense, calorie-rich foods such as lean proteins, whole grains, healthy fats, and complex carbohydrates.\n" +
                    "\n" +
                    "3. Balanced Diet: Maintain a well-balanced diet that includes a variety of food groups. Prioritize lean meats, fish, legumes, whole-grain products, dairy or dairy alternatives, and plenty of fruits and vegetables.\n" +
                    "\n" +
                    "4. Regular, Nutrient-Rich Meals: Eat multiple meals and snacks throughout the day to meet your caloric goals. Include protein-rich foods to support muscle development and overall health.\n" +
                    "\n" +
                    "5. Strength Training: Incorporate strength training into your fitness regimen to build muscle mass. Gaining muscle can contribute to healthy weight gain and enhance overall well-being."
        }
        else if(bmi.compareTo(18.5f)>0 && bmi.compareTo(25f)<=0){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
            maletips = "1. Strength Training: Include strength training exercises in your fitness routine to build and maintain muscle mass, which can boost your metabolism and overall physical health.\n" +
                    "\n" +
                    "2. Balanced Diet: Consume a balanced diet rich in lean proteins, whole grains, fruits, and vegetables. Pay attention to portion control and minimize processed foods and sugary beverages.\n" +
                    "\n" +
                    "3. Stay Active: Continue to engage in regular physical activity, such as cardio exercises, to maintain cardiovascular health and endurance.\n" +
                    "\n" +
                    "4. Regular Check-ups: Schedule regular health check-ups and screenings to monitor your health and catch any potential issues early.\n" +
                    "\n" +
                    "5. Mental Health: Pay attention to your mental and emotional well-being. Practice stress management techniques and seek support if you're facing mental health challenges.\n"
            femaletips = "1. Strength Training: Incorporate strength training into your fitness routine to build and tone muscles, which can improve metabolism and help maintain a healthy weight.\n" +
                    "\n" +
                    "2. Balanced Diet: Consume a well-balanced diet that includes plenty of fruits, vegetables, lean proteins, whole grains, and healthy fats. Focus on nutrient-rich foods for overall health.\n" +
                    "\n" +
                    "3. Cardiovascular Exercise: Engage in regular cardiovascular exercises to support heart health and maintain endurance.\n" +
                    "\n" +
                    "4. Regular Check-ups: Prioritize regular health check-ups, including women's health screenings, to ensure your overall well-being.\n" +
                    "\n" +
                    "5. Mental Health: Pay attention to your mental and emotional health. Practice mindfulness, meditation, or relaxation techniques to manage stress and maintain emotional well-being."
        }
        else if(bmi.compareTo(25f)>0 && bmi.compareTo(30f)<=0){
            bmiLabel = "OverWeight"
            bmiDescription = "Oops! You really Need to take care of yourself! Workout more!"
            maletips = "1. Regular Exercise: Engage in a consistent exercise routine that includes a mix of cardio, strength training, and flexibility exercises. Aim for at least 150 minutes of moderate-intensity aerobic exercise or 75 minutes of vigorous-intensity exercise per week.\n" +
                    "\n" +
                    "2. Balanced Diet: Consume a well-balanced diet with a variety of whole foods, including lean proteins, whole grains, fruits, and vegetables. Limit the intake of processed and high-sugar foods.\n" +
                    "\n" +
                    "3. Portion Control: Be mindful of portion sizes to avoid overeating. Use smaller plates and practice portion control to manage calorie intake.\n" +
                    "\n" +
                    "4. Stay Hydrated: Drink plenty of water throughout the day to help control appetite and stay properly hydrated. Limit sugary drinks and alcohol.\n" +
                    "\n" +
                    "5. Manage Stress: Find healthy ways to manage stress, such as through relaxation techniques, hobbies, or physical activities. High stress levels can lead to emotional eating and weight gain."
            femaletips = "1. Regular Exercise: Incorporate regular physical activity into your daily routine, including aerobic exercises, strength training, and flexibility workouts. Strive for at least 150 minutes of moderate-intensity aerobic exercise each week.\n" +
                    "\n" +
                    "2. Nutrient-Rich Diet: Focus on a diet rich in nutrients, including lean proteins, whole grains, fruits, vegetables, and healthy fats. Avoid crash diets and prioritize long-term, sustainable eating habits.\n" +
                    "\n" +
                    "3. Portion Control: Practice portion control to prevent overeating. Be mindful of serving sizes, especially when dining out.\n" +
                    "\n" +
                    "4. Hydration: Stay well-hydrated by drinking an adequate amount of water daily. Limit sugary drinks and opt for herbal teas or infused water.\n" +
                    "\n" +
                    "5. Emotional Well-Being: Pay attention to your emotional well-being and avoid using food as a coping mechanism for stress. Consider mindfulness practices, meditation, or counseling to address emotional triggers."
        }
        else if(bmi.compareTo(30f)>0 && bmi.compareTo(35f)<=0){
            bmiLabel = "Obese Class | (Moderately Obese)"
            bmiDescription = "Oops! You really Need to take care of yourself! Workout more!"
            maletips = "1. Consult a Healthcare Professional: If you are classified as \"Obese Class 1,\" it's crucial to consult with a healthcare professional, such as a doctor or a registered dietitian, to create a personalized plan tailored to your specific needs.\n" +
                    "\n" +
                    "2. Dietary Changes: Focus on making healthier food choices. Reduce the intake of processed foods, sugary beverages, and high-fat items. Opt for a balanced diet with plenty of fruits, vegetables, lean proteins, and whole grains.\n" +
                    "\n" +
                    "3. Portion Control: Pay attention to portion sizes and avoid overeating. Eating in moderation is key to managing calorie intake.\n" +
                    "\n" +
                    "4. Regular Exercise: Incorporate regular physical activity into your routine. Aim for at least 150 minutes of moderate-intensity aerobic exercise or 75 minutes of vigorous-intensity exercise per week.\n" +
                    "\n" +
                    "5 Strength Training: Include strength training exercises to build muscle, which can boost your metabolism and help with weight loss."
            femaletips = "1. Consult a Healthcare Professional: Seek guidance from a healthcare provider or a registered dietitian to develop a customized weight management plan tailored to your specific needs and health conditions.\n" +
                    "\n" +
                    "2. Balanced Diet: Focus on a balanced and nutritious diet that includes a variety of whole foods like fruits, vegetables, lean proteins, whole grains, and healthy fats. Limit your consumption of sugary and high-fat foods.\n" +
                    "\n" +
                    "3. Portion Control: Practice portion control to prevent overeating and help manage your calorie intake. Using smaller plates can be helpful.\n" +
                    "\n" +
                    "4. Regular Exercise: Incorporate regular physical activity into your routine. Include both aerobic exercises and strength training to support weight loss and improve overall fitness.\n" +
                    "\n" +
                    "5. Mindful Eating: Pay attention to what and when you eat. Practicing mindful eating can help you recognize hunger and fullness cues, which may aid in controlling overeating."

        }
        else if(bmi.compareTo(35f)>0 && bmi.compareTo(40f)<=0){
            bmiLabel = "Obese Class | (Severely Obese)"
            bmiDescription = "OMG! You are in Dangerous Condition! Act Now!"
            maletips = "1. Consult a Healthcare Professional: If you are classified as \"Obese Class I,\" it's essential to consult with a healthcare professional, such as a doctor, registered dietitian, or fitness expert, to create a personalized weight management plan tailored to your specific needs.\n" +
                    "\n" +
                    "2. Balanced Diet: Adopt a balanced and nutritious diet that emphasizes whole foods, including fruits, vegetables, lean proteins, whole grains, and healthy fats. Minimize the consumption of processed and high-calorie foods.\n" +
                    "\n" +
                    "3. Portion Control: Be mindful of portion sizes to prevent overeating. Using smaller plates can help you control portions and reduce calorie intake.\n" +
                    "\n" +
                    "4. Regular Exercise: Incorporate regular physical activity into your routine. Aim for a combination of aerobic exercises, like brisk walking or cycling, and strength training to help with weight loss and muscle development.\n" +
                    "\n" +
                    "5. Behavioral Changes: Identify and address emotional or psychological triggers for overeating. Consider working with a therapist or counselor to develop strategies for managing stress, emotional eating, and other factors contributing to excess weight."

            femaletips = "1 Consult a Healthcare Professional: Seek guidance from a healthcare provider, registered dietitian, or a weight management specialist to create a personalized plan tailored to your unique needs and health conditions.\n" +
                    "\n" +
                    "2. Balanced Diet: Embrace a well-balanced diet that includes a variety of nutrient-dense foods. Focus on fruits, vegetables, lean proteins, whole grains, and healthy fats while limiting the intake of sugary and high-fat foods.\n" +
                    "\n" +
                    "3. Portion Control: Practice portion control to prevent overeating and manage calorie intake effectively. Smaller, portion-appropriate plates can help with this.\n" +
                    "\n" +
                    "4. Regular Exercise: Include regular physical activity in your routine. A mix of aerobic activities, such as walking, swimming, or dancing, along with strength training, can aid in weight management and overall health.\n" +
                    "\n" +
                    "5. Mindful Eating: Develop mindful eating habits. Pay attention to hunger and fullness cues and avoid distracted or emotional eating. Mindful eating can help you make healthier food choices and control portion sizes."
        }else {
            bmiLabel = "Obese Class | (Very Severely Obese)"
            bmiDescription = "OMG! You are in Dangerous Condition! Act Now!"
            maletips = "1. Consult a Healthcare Professional: Consult with a healthcare provider, such as a doctor, registered dietitian, or obesity specialist, to create a comprehensive and personalized weight management plan.\n" +
                    "\n" +
                    "2. Dietary Changes: Focus on making healthier food choices. Eliminate or significantly reduce the consumption of processed foods, sugary beverages, and high-fat items. Prioritize a diet rich in fruits, vegetables, lean proteins, whole grains, and healthy fats.\n" +
                    "\n" +
                    "3. Portion Control: Pay attention to portion sizes to avoid overeating. This can be achieved by using smaller plates and measuring food quantities.\n" +
                    "\n" +
                    "4. Regular Exercise: Incorporate regular physical activity into your daily routine. Aim for at least 150 minutes of moderate-intensity aerobic exercise or 75 minutes of vigorous-intensity exercise per week, along with strength training.\n" +
                    "\n" +
                    "5. Behavioral Support: Consider joining support groups or working with mental health professionals to address any emotional or psychological factors contributing to overeating and obesity. Developing healthy coping mechanisms is essential."
            femaletips = "1. Consult a Healthcare Professional: Seek guidance from a healthcare provider, registered dietitian, or specialist in weight management to create a tailored plan that addresses your specific health needs.\n" +
                    "\n" +
                    "2. Balanced Diet: Emphasize a balanced diet that prioritizes whole, nutrient-dense foods while reducing the intake of processed and high-calorie options. Aim for a diet rich in fruits, vegetables, lean proteins, whole grains, and healthy fats.\n" +
                    "\n" +
                    "3. Portion Control: Practice portion control and mindful eating to manage calorie intake effectively. Smaller plates and paying attention to hunger cues can be helpful.\n" +
                    "\n" +
                    "4. Regular Exercise: Incorporate regular physical activity into your daily routine, combining both aerobic exercises and strength training. This can help with weight management and overall health.\n" +
                    "\n" +
                    "5. Emotional Well-Being: Address emotional or psychological triggers for overeating. Consider working with a therapist or counselor to develop strategies for managing stress, emotional eating, and other factors contributing to excess weight."
        }
        val bmivalue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text = bmivalue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvMBIDescription?.text = bmiDescription
        binding?.malestipspoints?.text = maletips
        binding?.femalestipspoints?.text = femaletips

        binding?.lltips?.visibility = View.VISIBLE


    }
    private fun validatemetricunit():Boolean{
        var isvalid = true
        if(binding?.etMetricWeight?.text.toString().isEmpty()){
            isvalid = false
        }else if(binding?.etMetricheight?.text.toString().isEmpty()){
            isvalid = false
        }
        return isvalid
    }
    private fun calculateUnits(){
        if(currentvisibleview== Metric_Units_View){
            if(validatemetricunit()){
                val heightvalue: Float = binding?.etMetricheight?.text.toString().toFloat()/100

                val weightvalue: Float = binding?.etMetricWeight?.text.toString().toFloat()

                val bmi = weightvalue/(heightvalue*heightvalue)

                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BMIActivity,"Please enter Vaild Values", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(validateUsunit()){
                val usUnitHeightvalueFeet:String = binding?.etUSmetricheightfeet?.text.toString()
                val usUnitHeightvalueInch: String = binding?.etUSmetricheightinch?.text.toString()
                val usUnitWeightvalue: Float= binding?.etUsMetricUnitWeight?.text.toString().toFloat()

                val heightvalue = (usUnitHeightvalueFeet.toFloat()*12) + usUnitHeightvalueInch.toFloat()

                val bmi = 703*(usUnitWeightvalue / (heightvalue*heightvalue))
                displayBMIResult(bmi)
            }
            else{
                Toast.makeText(this@BMIActivity,"Please enter Vaild Values", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUsunit():Boolean{
        var isvalid = true
        if(binding?.etUSmetricheightfeet?.text.toString().isEmpty()){
            isvalid = false
        }else if(binding?.etUSmetricheightinch?.text.toString().isEmpty()){
            isvalid = false
        }
        else if(binding?.etUsMetricUnitWeight?.text.toString().isEmpty()){
            isvalid = false
        }
        return isvalid
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}