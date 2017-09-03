package com.jdkgroup.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jdkgroup.cleanup.BaseDrawerActivity;
import com.jdkgroup.cleanup.R;
import com.jdkgroup.cleanup.fragment.CustomerCreateFragment;
import com.jdkgroup.cleanup.fragment.CustomerDetailsFragment;
import com.jdkgroup.cleanup.fragment.CustomerEditFragment;
import com.jdkgroup.cleanup.fragment.CustomerListFragment;
import com.jdkgroup.cleanup.fragment.FeedbackFragment;
import com.jdkgroup.cleanup.fragment.HomeFragment;
import com.jdkgroup.cleanup.fragment.InvoiceListFragment;
import com.jdkgroup.cleanup.fragment.LeadCreateBasicEditFragment;
import com.jdkgroup.cleanup.fragment.LeadCreateBasicFragment;
import com.jdkgroup.cleanup.fragment.LeadCreateFragment;
import com.jdkgroup.cleanup.fragment.LeadDetailsFragment;
import com.jdkgroup.cleanup.fragment.LeadEditFragment;
import com.jdkgroup.cleanup.fragment.LeadListFragment;
import com.jdkgroup.cleanup.fragment.LeadSearchToCreateFragment;
import com.jdkgroup.cleanup.fragment.MeetingListFragment;
import com.jdkgroup.cleanup.fragment.OpportunityDetailsFragment;
import com.jdkgroup.cleanup.fragment.OpportunityListFragment;
import com.jdkgroup.cleanup.fragment.QuotationCreateFragment;
import com.jdkgroup.cleanup.fragment.QuotationDetailsFragment;
import com.jdkgroup.cleanup.fragment.QuotationListFragment;
import com.jdkgroup.cleanup.fragment.QuotationSearchToCreateFragment;
import com.jdkgroup.cleanup.fragment.TaskListFragment;
import com.jdkgroup.cleanup.fragment.VerifyOTPFragment;
import com.jdkgroup.cleanup.model.OSInfo;
import com.jdkgroup.connection.RestConstant;
import com.jdkgroup.customviews.gson.ListOfJson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class AppUtils {
    private static String TAG = "Tag";
    private static int screenWidth = 0;

    private static CharSequence charsequence;
    private static JSONObject jsonobject;
    private static Iterator iterator;
    private static Gson gson;
    private static GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    public static String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isEmpty(EditText editText) {
        return getText(editText).equalsIgnoreCase("");
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public static void showToastById(Context context, int id) {
        Toast toast = Toast.makeText(context, getStringFromId(context, id), Toast.LENGTH_SHORT);
        TextView v = toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public static String getStringFromId(Context mContext, int id) {
        String str = null;
        try {
            str = mContext.getString(id);
        } catch (Exception e) {
        }
        return str;
    }

    public static void toast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void logD(String text) {
        Log.d(TAG, text);
    }

    public static void loge(String text) {
        Log.e(TAG, text);
    }

    public static android.support.v7.app.AlertDialog createAlertDialog(Activity activity, String message, String positiveText, String negativeText, DialogInterface.OnClickListener mDialogClick) {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(activity).setPositiveButton(positiveText, mDialogClick)
                        .setNegativeButton(negativeText, mDialogClick)
                        .setMessage(message);
        return builder.create();
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint ("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            showToast(context, context.getString(R.string.no_internet_message));
            return false;
        }
        return true;
    }

  /*  public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }*/

    @SuppressLint ("MissingPermission")
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint ("MissingPermission") Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void hideKeyboard(Activity ctx) {
        try {
            ctx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ctx.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            imm.hideSoftInputFromWindow(ctx.getWindow().getDecorView().getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            imm.hideSoftInputFromWindow(ctx.getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showKeyboard(Activity activity, AppCompatEditText view) {
        Context context = activity;
        try {
            if (context != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            Log.e("Exception on  show", e.toString());
        }
    }

    public static void requestEdittextFocus(Activity activity, AppCompatEditText view) {
        view.requestFocus();
        showKeyboard(activity, view);
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


//    public static FileUri createImageFile(String prefix) {
//        FileUri fileUri = new FileUri();
//
//        File image = null;
//        try {
//            image = File.createTempFile(prefix + String.valueOf(System.currentTimeMillis()), ".jpg", getWorkingDirectory());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (image != null) {
//            fileUri.setFile(image);
//            fileUri.setImageUrl(Uri.parse("file:" + image.getAbsolutePath()));
//        }
//        return fileUri;
//    }

    private static File getWorkingDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), "Mowadcom");
        return createDirectory(directory);
    }

    private static File createDirectory(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static String setImage(String imageBaseUrl) {
        // return RestConstant.IMAGE_BASE_URL.concat(imageBaseUrl);
        return null;
    }


   /* public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }
    public static String setImage(String url)
    {
        return RestConstant.IMAGE_URL+url;
    }*/

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }

    public static void startActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }

    public static SpannableString timestampToDate(String strTimestamp) {
        try {
            long timestamp = Long.parseLong(strTimestamp) * 1000L;
            DateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm aaa");
            Date netDate = (new Date(timestamp));
            String suffix = getDayOfMonthSuffix(netDate.getDate());

            String[] str = sdf.format(netDate).split(" ");
            String strDate = "";
            for (int i = 0; i < str.length; i++) {
                if (i == 0)
                    strDate = str[i] + suffix + " ";
                else if (i == str.length - 1)
                    strDate = strDate + str[i];
                else
                    strDate = strDate + str[i] + " ";
            }

            SpannableString styledString = new SpannableString(strDate);
            styledString.setSpan(new SuperscriptSpan(), 2, 4, 0);
            styledString.setSpan(new RelativeSizeSpan(0.7f), 2, 4, 0);
            styledString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strDate.length(), 0);
            return styledString;
        } catch (Exception ex) {
            return new SpannableString("");
        }
    }

    static String getDayOfMonthSuffix(final int n) {

        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String getDateFromTime(long mTimestamp, String mDateFormate) {
        String date;
        SimpleDateFormat dateFormatter = new SimpleDateFormat(mDateFormate);
        dateFormatter.setTimeZone(TimeZone.getDefault());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(mTimestamp);

        date = dateFormatter.format(cal.getTime());
        return date;
    }

    public static String getDateString(long miliis, String mDateFormate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(miliis);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(mDateFormate, Locale.ENGLISH);
        return dateFormatter.format(calendar.getTime());
    }

    public static long getUtC(long millis) {
        return millis + TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
    }

    public static long getGMT(long millis) {
        return millis - TimeZone.getDefault().getRawOffset() - TimeZone.getDefault().getDSTSavings();
    }

    public static String getDate(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("DD MMMM yyyy", Locale.getDefault());
            Date netDate = (new Date(timeStamp * 1000));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "";
        }
    }

    public static long getDateTimeInMilliseconds(final String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy");
        Date mDate = sdf.parse(date);
        return mDate.getTime();
    }

    public static String getFormatedNumber(String val) {
        double d = 0;
        try {
            d = Double.parseDouble(val);
        } catch (Exception e) {
            d = 0.00;
        }
        // return String.format("%.2f",NumberFormat.getNumberInstance(Locale.UK).format(d));
        return NumberFormat.getNumberInstance(Locale.UK).format(d) + "";
    }

//    public static String stringToSHA1(String text) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            byte[] sha1hash = new byte[40];
//            md.update(text.getBytes("iso-8859-1"), 0, text.length());
//            sha1hash = md.digest();
//            return convertToHex(sha1hash);
//        } catch (Exception e) {
//
//        }
//        return "";
//    }

    public static CharSequence randomUUID() {
        charsequence = java.util.UUID.randomUUID().toString();
        return charsequence;
    }

    public static JSONObject ConvertMapToJsonObject(final Map map) {
        jsonobject = new JSONObject();
        try {
            iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();

                jsonobject.put(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
            }
        } catch (Exception ex) {
        }

        return jsonobject;
    }

    public static String removeAllWhiteSpace(String value) {
        return value.replaceAll("\\s+", "");
    }

    public void FacebookHashKey(Activity activity, String packageName) {
        PackageInfo info;
        try {
            info = activity.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String facebookkeyBase64 = new String(Base64.encode(md.digest(), 0));
                //String facebookkeyBase64new = new String(Base64.encodeBytes(md.digest()));
            }
        } catch (PackageManager.NameNotFoundException e1) {

        } catch (NoSuchAlgorithmException e) {

        } catch (Exception e) {

        }
    }

    public static String readFileFromAssets(final Activity activity, final String path, final String extension) throws Exception {
        String str = null;
        try {
            InputStream is = activity.getAssets().open(path + "." + extension);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return str;
    }

    /* VALIDATION */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static boolean isValidationRegularExpression(final String regularexpression, final String value) {
        return value.matches(regularexpression);
    }

    public static String getPathFromMediaUri(@NonNull Context context, @NonNull Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static File decreaseImageSize(File file) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            final int REQUIRED_SIZE = 75;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static long getFileSize(File file) {
        long sizeInBytes = file.length();
        return sizeInBytes / (1024 * 1024);
    }

    private Gson SwitchGson(int param) {
        switch (param) {
            case 1:
                gson = gsonBuilder.create();
                return gson;

            case 2: //FIRST CHARACTER UPPER CAMEL
                gson = new GsonBuilder().
                        disableHtmlEscaping().
                        setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).
                        setPrettyPrinting().serializeNulls().
                        create();
                return gson;

            default:
                break;
        }
        return null;
    }

    public static int dpToPx(final float dp, final Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public void NavigationBarBackground(final Activity activity, int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, id));
        }
    }

    public static void replaceFragment(Activity activity, Fragment fragment, Bundle bundle) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager fragmentManager = ((BaseDrawerActivity) activity).getSupportFragmentManager();

        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.frameLayout, fragment, backStateName);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    public static void lauchFramgentActivity(final Activity activity, final int redirect) {
        Fragment fragment;

        switch (redirect) {
            case 0: //LOGOUT

                break;

            case 1_0: //HOME
                fragment = new HomeFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 1_0_1: //VERIFY OTP
                fragment = new VerifyOTPFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 1: //CUSTOMER LIST
                fragment = new CustomerListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 1_1: //CUSTOMER CREATE
                fragment = new CustomerCreateFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 1_2: //CUSTOMER DETAILS
                fragment = new CustomerDetailsFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 1_3: //CUSTOMER  EDIT
                fragment = new CustomerEditFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2: //LEAD LIST
                fragment = new LeadListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_1: //LEAD SEARCH TO CREATE
                fragment = new LeadSearchToCreateFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_2: //LEAD CREATE BASIC
                fragment = new LeadCreateBasicFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_3: //LEAD CREATE
                fragment = new LeadCreateFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_4: //LEAD DETAILS
                fragment = new LeadDetailsFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_5: //LEAD CREATE BASIC EDIT
                fragment = new LeadCreateBasicEditFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 2_6: //LEAD EDIT
                fragment = new LeadEditFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 3: //OPPORTUNITY LIST
                fragment = new OpportunityListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 3_1: //OPPORTUNITY DETAILS
                fragment = new OpportunityDetailsFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 4: //QUOTATION LIST
                fragment = new QuotationListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 4_1: //QUOTATION SEARCH TO CREATE
                fragment = new QuotationSearchToCreateFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 4_2: //QUOTATION DETAILS
                fragment = new QuotationDetailsFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 4_3: //QUOTATION CREATE
                fragment = new QuotationCreateFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 5: //INVOICE LIST
                fragment = new InvoiceListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 6: //MEETING LIST
                fragment = new MeetingListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 7: //TASK LIST
                fragment = new TaskListFragment();
                replaceFragment(activity, fragment, null);
                break;

            case 100_1: //FEEDBACK
                fragment = new FeedbackFragment();
                replaceFragment(activity, fragment, null);
                break;

            default:
                System.out.println("No available drawer menu");
        }
    }

    public static String getToJson(List<?> alData) {
        gson = gsonBuilder.create();
        return gson.toJson(alData);
    }

    public static String getToJsonClass(Object src) {
        gson = gsonBuilder.create();
        return gson.toJson(src);
    }

    public static <T> T getFromJson(String str, Class<T> classType) {
        return new Gson().fromJson(str, classType);
    }

    public static void displayMap(HashMap<String, String> hashMap) {
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            AppUtils.loge("DISPLAY MAP " + RestConstant.API_PARAMETER + pair.getKey() + pair.getValue());
        }
    }

    public static <T> List<T> getToList(String json, Class<T> typeClass) {
        gson = gsonBuilder.create();
        return gson.fromJson(json, new ListOfJson<T>(typeClass));
    }

    //TODO DUPLICATE REMOVE ITEMS
    public static <E> List<E> listRemoveDuplicates(List<E> list) {
        Set<E> uniques = new HashSet<E>();
        uniques.addAll(list);
        return new ArrayList<E>(uniques);
    }

    public static <E> List<E> linkedHashSetRemoveDuplicates(List<E> list) {
        return new ArrayList<E>(new LinkedHashSet<>(list));
    }

    public static void getDeviceInfo(Activity activity) {
        PackageInfo packageInfo;
        String deviceUniqueId, deviceType, deviceName, osVersion, appVersion = "", countryIso, networkOperatorName;

        deviceUniqueId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceType = "Android";
        deviceName = Build.BRAND + Build.MODEL;
        osVersion = Build.VERSION.RELEASE;

        try {
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        countryIso = tm.getNetworkCountryIso();
        networkOperatorName = tm.getNetworkOperatorName();

        List<OSInfo> alOSInfo = new ArrayList<>();
        alOSInfo.add(new OSInfo(deviceUniqueId, deviceType, deviceName, osVersion, appVersion, countryIso, networkOperatorName));

        logD("OS Information " + getToJsonClass(alOSInfo));
    }

    public static void appExist(final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setMessage(activity.getString(R.string.dialog_message_app_exist));
        alert.setPositiveButton(activity.getString(R.string.dialog_exist), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
                activity.moveTaskToBack(true);
                System.exit(1);
            }
        });
        alert.setNegativeButton(activity.getString(R.string.dialog_cancel), null);
        alert.show();
    }
}
