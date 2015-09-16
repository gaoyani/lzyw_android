package cat.projects.dmc_service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import com.dlna.datadefine.DLNA_DeviceData;

/**
 * @author Administrator Util class 用于upnp的工具接口
 */
public class DMCUtil {

	public static final int CONTENT_FMT_UNKNOWN = 0x0001;
	public static final int CONTENT_FMT_VIDEO = 0x0002;
	public static final int CONTENT_FMT_AUDIO = 0x0004;
	public static final int CONTENT_FMT_PHOTO = 0x0008;
	public static final int CONTENT_FMT_DIR = 0x0010;

	public static List<String> videoFmt = null;
	public static List<String> audioFmt = null;
	public static List<String> photoFmt = null;

	public static int fileFmt(String _filename) {
		if (photoFmt == null) {
			photoFmt = new ArrayList<String>();
			photoFmt.add(".jpg");
			photoFmt.add(".jpeg");
			photoFmt.add(".bmp");
			photoFmt.add(".png");
			photoFmt.add(".tiff");
			photoFmt.add(".tga");
			photoFmt.add(".exif");
			photoFmt.add(".gif");
			photoFmt.add(".tif");
			photoFmt.add(".ppm");
			photoFmt.add(".qti");
			photoFmt.add(".qtf");
			photoFmt.add(".jpe");
			photoFmt.add(".ico");
			photoFmt.add(".pcd");
			photoFmt.add(".pnm");
			photoFmt.add(".qtif");
			photoFmt.add(".psd");
		}
		if (audioFmt == null) {
			audioFmt = new ArrayList<String>();
			audioFmt.add(".mpa");
			audioFmt.add(".wma");
			audioFmt.add(".mp2");
			audioFmt.add(".wav");
			audioFmt.add(".midi");
			audioFmt.add(".acm");
			audioFmt.add(".aif");
			audioFmt.add(".aifc");
			audioFmt.add(".aiff");
			audioFmt.add(".mp3");
			audioFmt.add(".wma");
			audioFmt.add(".ra");
			audioFmt.add(".flac");
			audioFmt.add(".ape");
			audioFmt.add(".ogg");
			audioFmt.add(".oga");
			audioFmt.add(".tta");
			audioFmt.add(".mpc");
			audioFmt.add(".m4a");
			audioFmt.add(".m4r");
			audioFmt.add(".m4p");
			audioFmt.add(".m4b");
			audioFmt.add(".3g2");
			audioFmt.add(".acc");
			audioFmt.add(".ac3");
			audioFmt.add(".pcm");
			audioFmt.add(".snd");
			audioFmt.add(".at3p");
			audioFmt.add(".au");
			audioFmt.add(".dts");
			audioFmt.add(".rmi");
			audioFmt.add(".mid");
			audioFmt.add(".mp1");
			audioFmt.add(".lpcm");
			audioFmt.add(".mka");
			audioFmt.add(".ram");
			audioFmt.add(".m3u");
		}
		if (videoFmt == null) {
			videoFmt = new ArrayList<String>();
			videoFmt.add(".m3u8");
			videoFmt.add(".ts");
			videoFmt.add(".asf");
			videoFmt.add(".avc");
			videoFmt.add(".avi");
			videoFmt.add(".dv");
			videoFmt.add(".divx");
			videoFmt.add(".xvid");
			videoFmt.add(".wmv");
			videoFmt.add(".mjpg");
			videoFmt.add(".mjpeg");
			videoFmt.add(".mpeg");
			videoFmt.add(".mpg");
			videoFmt.add(".mpe");
			videoFmt.add(".mp2p");
			videoFmt.add(".vob");
			videoFmt.add(".mp2t");
			videoFmt.add(".m1v");
			videoFmt.add(".m2v");
			videoFmt.add(".m4v");
			videoFmt.add(".mpg2");
			videoFmt.add(".mpeg2");
			videoFmt.add(".m4p");
			videoFmt.add(".mp4ps");
			videoFmt.add(".ogm");
			videoFmt.add(".xpm");
			videoFmt.add(".mkv");
			videoFmt.add(".rmvb");
			videoFmt.add(".mov");
			videoFmt.add(".hdmov");
			videoFmt.add(".mp4");
			videoFmt.add(".rm");
			videoFmt.add(".3gp");
			videoFmt.add(".3gpp");
			videoFmt.add(".tp");
			videoFmt.add(".m2p");
			videoFmt.add(".m2ts");
			videoFmt.add(".flv");
			videoFmt.add(".f4v");
			videoFmt.add(".hlv");
			videoFmt.add(".h4v");
			videoFmt.add(".dat");
		}
		int _dot = _filename.lastIndexOf(".");
		if (_dot < 0)
			return CONTENT_FMT_UNKNOWN;
		_filename = _filename.substring(_dot);
		int i, size = videoFmt.size();
		for (i = 0; i < size; i++) {
			if (_filename.compareToIgnoreCase(videoFmt.get(i)) == 0)
				return CONTENT_FMT_VIDEO;
		}
		size = audioFmt.size();
		for (i = 0; i < size; i++) {
			if (_filename.compareToIgnoreCase(audioFmt.get(i)) == 0)
				return CONTENT_FMT_AUDIO;
		}
		size = photoFmt.size();
		for (i = 0; i < size; i++) {
			if (_filename.compareToIgnoreCase(photoFmt.get(i)) == 0)
				return CONTENT_FMT_PHOTO;
		}
		return CONTENT_FMT_UNKNOWN;
	}

	public static String getDidlFmtString(String name, String fmtstr,
			String metastr, String uri, long metasize,
			boolean auth/* Optional */, String passwd/* Optional */) {
		String linestr = "";
		linestr = "<DIDL-Lite xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\">";
		linestr += "<item id=\"1\" parentID=\"-1\" restricted=\"1\">";
		linestr += "<upnp:storageMedium>UNKNOWN</upnp:storageMedium>";
		linestr += "<upnp:writeStatus>UNKNOWN</upnp:writeStatus>";
		linestr += "<dc:title>" + name + "</dc:title>";
		linestr += "<upnp:class>" + fmtstr + "</upnp:class>";
		linestr += "<res protocolInfo=\"http-get:*:" + metastr + ":*\"";
		if (metasize > 0) {
			linestr += " size=\"" + metasize + "\"";
		}
		linestr += ">" + uri + "</res>";
		if (auth == true && passwd != null && passwd.length() > 0) {
			linestr += "<upnp:password>" + passwd + "</upnp:password>";
		}
		linestr += "</item>";
		linestr += "</DIDL-Lite>";
		return linestr;
	}

	public static void DoSleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean isSameNetwork(String ipsrc, String ipdst) {
		if (ipsrc == null)
			return true;
		if (ipdst == null)
			return false;
		int slash1 = ipsrc.lastIndexOf('.');
		int slash2 = ipdst.lastIndexOf('.');
		if (slash1 > 0 && slash2 > 0) {
			String net1 = ipsrc.substring(0, slash1);
			String net2 = ipdst.substring(0, slash2);
			return net1.equals(net2);
		}
		return false;
	}

	// 获得本地ip
	public static String getLocalIpAddr(String dstIp) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					String localIp = inetAddress.getHostAddress().toString();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(localIp)
							&& DMCUtil.isSameNetwork(localIp, dstIp))
						return localIp;
				}
			}
		} catch (SocketException es) {
			es.printStackTrace();
		}
		return "127.0.0.1";
	}

	public static String convertToTime(int time) {
		int h = time / 3600;
		int m = time % 3600 / 60;
		int s = time % 60;
		String result = String.format("%02d:%02d:%02d", h, m, s);
		return result;
	}
	
	public static  DLNA_DeviceData clone_DLNA_DeviceData(DLNA_DeviceData _device) {
		DLNA_DeviceData devinst = new DLNA_DeviceData();
		devinst.devicename = new String(_device.devicename);
		devinst.iconurl = new String(_device.iconurl);
		devinst.localip = new String(_device.localip);
		devinst.serialNumber = new String(_device.serialNumber);
		devinst.type = _device.type;
		devinst.URN = new String(_device.URN);
		devinst = _device;
		return devinst;
	}

}
